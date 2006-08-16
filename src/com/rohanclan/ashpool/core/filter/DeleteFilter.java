/*
 * Ashpool - XML Database
 * Copyright (C) 2003 Rob Rohan
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 * DeleteFilter.java
 *
 * Created on March 16, 2003, 8:10 PM
 */

package com.rohanclan.ashpool.core.filter; 

import java.util.HashMap;
import java.util.Map;
//import java.util.StringTokenizer;

import com.rohanclan.ashpool.core.AResultSet;
import com.rohanclan.ashpool.core.AshpoolSQLFilter;
import com.rohanclan.ashpool.core.CommandManager;
import com.rohanclan.ashpool.core.Functions;
import com.rohanclan.ashpool.core.TableManager;
import com.rohanclan.ashpool.core.xml.BasicXSLEngine;

//import org.xml.sax.helpers.XMLReaderFactory;

/**
 * filter to delete rows from a table
 * @author  rob
 */
public class DeleteFilter extends SQLFilter implements AshpoolSQLFilter {
	//private org.xml.sax.XMLReader reader;
	//private XMLtoResultSetFilter xmlfilter;
	
	private WhereFilter wf;
	private TableFilter tf;
	
	/** Creates a new instance of DeleteFilter */
	public DeleteFilter(TableManager tman, CommandManager com) throws Exception{
		super(tman, com);
		//reader = XMLReaderFactory.createXMLReader();
	}
	
	/** sets the xslt engine */
	public void setXSLEngine(BasicXSLEngine bxsl){
		this.bXSL = bxsl;
	}
	
	/** returns the filename of a temp file that has the results of this
	 * query, or if it is a single value, has the string results
	 */
	public String executeQuery(String sql) throws Exception{
	
		java.io.OutputStream os = null;
		String rsFile = null;
		
		//make a temp file
		rsFile = "!dtmp" + System.currentTimeMillis();
		tableman.createTable(rsFile,"", TableManager.TYPE_TABLE);
		
		//get a stream to it
		os = tableman.getTableOutputStream(rsFile, TableManager.TYPE_TABLE);
		/* os = new java.io.FileOutputStream(
			tableman.getTableFile(rsFile, TableManager.TYPE_TABLE)
		); */
		
		String sheet = createXPath(sql);
		if(comman.getGlobalVariable("SYS:DEBUG").toString().equalsIgnoreCase("true")){
			System.out.println(sheet);
		}
		//create a style sheet from the sql, and get it ready to transform
		java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(
			//createXPath(sql, type).getBytes("UTF-8")
			sheet.getBytes("iso-8859-1")
		);
		//make sure the engine knows the way to the datastore
		bXSL.setParam("datastore", "file://" + tableman.getDatastore().getAbsolutePath());
		
		//apply the stylesheet and get the results
		//long ttime = 
		bXSL.transform(tableman.getTableInputStream(mainfile),bais,os);
		
		os.flush();
		os.close();
		
		return rsFile;
	}
	
	 /** fills a result set with with the results of an sql->xslt query 
	 */
	public void executeQuery(String sql, AResultSet ars) throws Exception{
		//the temp file should have the full xmldocument filename with the
		//delete records removed
		String tmptablename = executeQuery(sql);
		
		synchronized("MODIFY_" + mainfile){
			//don't use drop table or the schema and icf file will go too!
			//manually delete the file
			tableman.getTableFile(mainfile, TableManager.TYPE_TABLE).delete();
			//now move the temp file to the original
			tableman.renameTable(tmptablename, mainfile);
		}
	}
	
	/** try to convert an sql select statement into an xslt stylesheet */
	public String createXPath(String sql) throws Exception{
		//StringTokenizer stok = new StringTokenizer(sql," ");
		//StringBuffer whereclause    = new StringBuffer();
		String whereclause    = new String();
		
		//this is kind of kludgy. If wherefilter is null then this is prolly
		//the first call, so make all our needed filters. (hopefully this
		//will speed things up a bit)
		if(wf == null){
			wf  = new WhereFilter(tableman, comman);
			tf  = new TableFilter(tableman, comman);
		}
		
		/* 
		delete * from test_tbl     where x = 1
		delete from test_tbl       where x = 1
		delete test_tbl            where x = 1
		*/
		
		String delete[] = new String[]{"",""};
		String from[]   = new String[]{"",""};
		String where[]  = new String[]{"",""};
		
		// 1)
		//save any user defined strings so they dont jack up our splits
		Map<String,String> savedStrings = new HashMap<String,String>();
		sql = Functions.placeHoldStrings(sql,savedStrings);
		
		////////////////////////////////////////////////////////////////////
		//the parser expects spaces between some elements, now that
		//strings are removed, adjust the query string
		sql = sql.replaceAll("\\(", " ( ");
		sql = sql.replaceAll("\\)", " ) ");
		sql = sql.replaceAll(",", ", ");
		////////////////////////////////////////////////////////////////////
		
		// 2)
		//make sure keywords are lower case and
		//escape subqueries and joins so they dont jack up our splits
		sql = Functions.unCaseKeywords(sql);
		sql = Functions.escapeSubQueries(sql);
		
		// 3)
		// split the query into it's parts
		where = sql.split("where");
		from = where[0].split("from");
		delete = from[0].split("delete");
		
		//sometimes delete has the file name sometimes from does, but if
		//done in this order all seems well
		if(delete.length > 1){
			tf.createXPath(delete[1]);
			//System.out.println("delete: " + delete[1]);
		}
		if(from.length > 1){
			tf.createXPath(from[1]);
			//System.out.println("from: " + from[1]);
		}
		//if there is a where clause have the where filter build us the xpath
		if(where.length > 1){
			//System.out.println("where: " + where[1]);
			//deletes invert the where clause
			wf.setNot(true);
			whereclause = wf.createXPath(
				Functions.unplaceHoldStrings(where[1],savedStrings)
			);
		//no where clause delete everything
		}else{
			whereclause = "[not(*)]";
		}
		
		mainfile = tf.getTableName();
		return S_DEL + whereclause.toString() + E_DEL;
	}
	
	private static final String S_DEL = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
		+ "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
		+ "<xsl:output method=\"xml\" indent=\"no\" encoding=\"UTF-8\"/>"
		+ "<xsl:template match=\"/\">"
		+ "<xsl:variable name=\"tname\">"
			+ "<xsl:value-of select=\"name(/*)\"/>"
		+ "</xsl:variable>"
		+ "<xsl:element name=\"{$tname}\">"
			+ "<xsl:apply-templates select=\"*/*";    //[not(*)]
	private static final String E_DEL = "\" />"
		+ "</xsl:element>"
	+ "</xsl:template>"
	+ "<xsl:template match=\"*/*\">"
		+ "<xsl:variable name=\"rname\">"
			+ "<xsl:value-of select=\"name(.)\"/>"
		+ "</xsl:variable>"
		+ "<xsl:element name=\"{$rname}\">"
			+ "<xsl:copy-of select=\"*\"/>"
		+ "</xsl:element>"
	+ "</xsl:template>"
	+ "<xsl:template match=\"text()\"/>"
	 + "</xsl:stylesheet>";
}