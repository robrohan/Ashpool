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
 * SelectFilter.java
 *
 * Created on February 1, 2003, 10:16 AM
 */

package com.rohanclan.ashpool.core.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
//import java.util.StringTokenizer;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.rohanclan.ashpool.core.AResultSet;
import com.rohanclan.ashpool.core.AshpoolSQLFilter;
import com.rohanclan.ashpool.core.CommandManager;
import com.rohanclan.ashpool.core.Functions;
import com.rohanclan.ashpool.core.TableManager;
import com.rohanclan.ashpool.core.xml.BasicXSLEngine;
import com.rohanclan.ashpool.core.xml.XMLtoResultSetFilter;

/**
 * Tries to take an SQL statement, turn it into a style sheet and apply it to
 * an xml file to get a ResultSet. Attempts to emulate an sql select statment
 * @author  rob
 */
public class SelectFilter extends SQLFilter implements AshpoolSQLFilter {
	public static final byte FORRESULTSET = 0;
	public static final byte FORSINGLE = 1;
	
	private XMLReader reader;
	private XMLtoResultSetFilter xmlfilter;
	
	private boolean doDistinct = false;
	
	private WhereFilter wf;
	private ColumnFilter cf;
	private OrderFilter of;
	private TableFilter tf;
	private JoinFilter jf;
	
	//any join commands
	private String join[];
	
	//a second select filter for subqueries. needed when the
	//sub query is calling the same table as the main query
	//but used for all subqueries.
	//private SelectFilter subS;
	
	/** Creates a new instance of SelectFilter */
	public SelectFilter(TableManager tman, CommandManager com) throws Exception{
		super(tman, com);
		//tableman = tman;
		//comman = com;
		reader = XMLReaderFactory.createXMLReader();
		
	}
	
	/** sets the table manager */
	public void setTableManager(TableManager tman){
		tableman = tman;
	}
	
	/** sets the xslt engine */
	public void setXSLEngine(BasicXSLEngine bxsl){
		this.bXSL = bxsl;
	}
	
	public SelectFilter copy(){
		try{
			SelectFilter sf = new SelectFilter(tableman, comman);
			sf.setXSLEngine(this.bXSL);
			return sf;
		}catch(Exception e){
			return null;
		}
	}
	
	/** gets what this table uses to mark the begining and ending of the
	 * &quot;table&quot;
	 */
	public String getTableMarker(String tablename) throws Exception{
		return applyStyleSheet(tablename, 2); //"getTableRootNode.xsl");
	}
	
	/** gets what this table uses to mark the begining and ending of it's
	 * &quot;rows&quot;
	 */
	public String getRowMarker(String tablename) throws Exception{
		return applyStyleSheet(tablename, 1); //"getTableRowNode.xsl");
	}
	
	/** applies a style sheet to a table and returns a simple value */
	private String applyStyleSheet(String tablename, int sheet) throws Exception{
		bXSL.clearParams();
	
		//get a result stream
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.io.InputStream is = null;
		//= this.getClass().getResourceAsStream(
		//    "/com/rohanclan/ashpool/libxslt/" + sheetname
		//);
		switch(sheet){
			case 1:
				is = new java.io.ByteArrayInputStream(
					com.rohanclan.ashpool.libxslt.CompiledSheets.sheetGetTableRowNode.getBytes() 
				);
				break;
			case 2:
				is = new java.io.ByteArrayInputStream(
					com.rohanclan.ashpool.libxslt.CompiledSheets.sheetGetTableRootNode.getBytes() 
				);
				break;
		}
		
		bXSL.transform(tableman.getTableInputStream(tablename),is,baos);
		
		baos.flush();
		baos.close();
		is.close();
		
		return new String(baos.toByteArray());
	}
	
	/** get the names, and types of a tables columns
	 */
	public void getTableColumns(String tablename, AResultSet ars) throws Exception {
		
		bXSL.clearParams();
		
		//get a result stream
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.io.InputStream is;
		
		bXSL.setParam("tablename", tablename + TableManager.TABLEEXT);
		
		if(tableman.hasSchema(tablename)){
			
			bXSL.setParam("schemaname", tablename + TableManager.TABLESCHEMA);
			//apply the stylesheet to the schema
			
			//get the stylesheet for a table with a schema
			is = new java.io.ByteArrayInputStream(
				com.rohanclan.ashpool.libxslt.CompiledSheets.sheetGetColumnNamesSchema.getBytes() 
			);
			
			//bXSL.transform(tableman.getSchemaInputStream(tablename),is,baos);
			bXSL.transform(
				tableman.getSchemaInputStream(tablename),
				is,
				baos
			);
		}else{
			//get the stylesheet for a table without a schema
			is = new java.io.ByteArrayInputStream(
				com.rohanclan.ashpool.libxslt.CompiledSheets.sheetGetColumnNames.getBytes() 
			);
			
			//apply the stylesheet to the table
			bXSL.transform(tableman.getTableInputStream(tablename),is,baos);
		}
		
		
		//create a new resultset filter
		xmlfilter = new XMLtoResultSetFilter(reader, ars);
		
		//make an out an in
		InputSource inputSource = new InputSource(
			new ByteArrayInputStream(baos.toByteArray())
		);
	
		//fill in the resultset with the trasfrom results
		xmlfilter.parse(inputSource);
		
		baos.flush();
		baos.close();
		is.close();
	}
	
	/** returns the filename of a temp file that has the results of this
	 * query, or if it is a single value select, has the results of the
	 * select
	 */
	public String executeQuery(String sql, byte type) throws Exception {
		
		java.io.OutputStream os = null;
		String rsFile = null;
		String dFile  = null;
		
		try{
			//if this is going to give a resultset type of xml doc, send it to
			//a temp file
			if(type == SelectFilter.FORRESULTSET){
				rsFile = "!stmp" + System.currentTimeMillis();
				
				tableman.createTable(rsFile,"", TableManager.TYPE_TABLE);
				
				os = tableman.getTableOutputStream(rsFile, TableManager.TYPE_TABLE);
				
				/* os = new java.io.FileOutputStream(
					tableman.getTableFile(rsFile, TableManager.TYPE_TABLE)
				); */
				
			//otherwise get a byte stream ready
			}else if(type == SelectFilter.FORSINGLE){
				os = new java.io.ByteArrayOutputStream();
			}
			
			////////////////////////////////////////////////////////////////
			
			String sheet = createXPath(sql, type);
			
			//show the xsl sheet if debug is on
			if(comman.getGlobalVariable("SYS:DEBUG").toString().equalsIgnoreCase("true")){
				System.out.println(sheet);
			}
			
			//create a style sheet from the sql, and get it ready to transform
			java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(
				//createXPath(sql, type).getBytes("UTF-8")
				sheet.getBytes("iso-8859-1")
			);
			
			//make sure the engine knows the way to the datastore
			//for joins...
			//this will not work for encrypted datastores! Need to design another
			//way for joins.
			bXSL.setParam("datastore", "file://" + tableman.getDatastore().getAbsolutePath());
			
			//apply the stylesheet and get the results
			//long ttime = 
			bXSL.transform(tableman.getTableInputStream(mainfile),bais,os);
			
			os.flush();
			os.close();
			
			//if the distinct flag is set clear out possible dupes
			if(doDistinct){
				dFile = "!sDtmp" + System.currentTimeMillis();
				
				tableman.createTable(dFile,"", TableManager.TYPE_TABLE);
				
				//java.io.InputStream fis = tableman.getTableInputStream(rsFile);
				java.io.BufferedInputStream fis = new java.io.BufferedInputStream(
					tableman.getTableInputStream(rsFile)
				);
				
				java.io.InputStream is = new java.io.ByteArrayInputStream(
					com.rohanclan.ashpool.libxslt.CompiledSheets.sheetDoDistinct.getBytes() 
				);
				
				java.io.OutputStream dos = tableman.getTableOutputStream(dFile, TableManager.TYPE_TABLE);
				
				bXSL.clearParams();
				//again... no point
				bXSL.setParam("datastore", "file://" + tableman.getDatastore().getAbsolutePath());
				
				//long distime = 
				bXSL.transform(fis, is, dos);
				
				fis.close();
				is.close();
				
				dos.flush();
				dos.close();
				
				//now set the file to be returned to the distincted file
				tableman.getTableFile(rsFile, TableManager.TYPE_TABLE).delete();
				rsFile = dFile;
				
				doDistinct = false;
			}
			
			////////////////////////////////////////////////////////////////
			
			if(type == SelectFilter.FORRESULTSET){
				return rsFile;
			}else if(type == SelectFilter.FORSINGLE){
				//this should have an extra comma - remove it
				String values = new String(((ByteArrayOutputStream)os).toByteArray());
				if(values.length() > 1){
					return values.substring(0,values.length()-1);
				}else{
					return "";
				}
			}
		}catch(Exception e){
			//clear out the temp file
			tableman.getTableFile(rsFile, TableManager.TYPE_TABLE).delete();
			if(dFile != null && dFile != rsFile){
				tableman.getTableFile(dFile, TableManager.TYPE_TABLE).delete();
			}
			//e.printStackTrace(System.err);
			//throw new java.sql.SQLException(e.toString());
			throw e;
		}
		return null;
	}
	
	/** fills the passed AResultSet with with the results of an sql->xslt query 
	 */
	public void executeQuery(String sql, AResultSet ars) throws Exception{
		//get the results of the sql into an input source
		String dFile = "";
		
		//teptablename now has the name of the temp file used to create a
		//result set. the "master" table.
		String tmptablename = executeQuery(sql, SelectFilter.FORRESULTSET);
		
		//do any joins to the new limited set
		if(join.length > 1){
			System.out.println("doing join");
			
			//loop over the join command and try to  do the joins
			int jl = join.length;
			for(int i=1; i<jl; i++){
			
				//create the file that will hold the results
				dFile = "!sJtmp" + System.currentTimeMillis();
				tableman.createTable(dFile,"", TableManager.TYPE_TABLE);
				
				java.io.OutputStream output = new java.io.FileOutputStream(
					tableman.getTableFile(dFile, TableManager.TYPE_TABLE)
				);
				
				//process the sql fragment
				jf.createXPath(join[i]);
				
				/* System.out.println(jf);
				System.out.println(tmptablename + " " 
					+ new File(tableman.getDatastore().getAbsolutePath() + "/" + tmptablename + ".xml").exists()
				); */
				
				//set up the sheet params
				bXSL.clearParams();
				bXSL.setParam("datastore", "file://" + tableman.getDatastore().getAbsolutePath());
				bXSL.setParam("j1", jf.getJoinField1());
				bXSL.setParam("j2", jf.getJoinField2());
				bXSL.setParam("t1", tmptablename);
				bXSL.setParam("t2", jf.getTableName());
				bXSL.setParam("type", jf.getType());
				bXSL.setParam("dir", jf.getDirection());
				
				//run the sheet
				java.io.InputStream fakexml = new java.io.ByteArrayInputStream(
					"<?xml version=\"1.0\" ?><zzzzzzzzzzzzz></zzzzzzzzzzzzz>".getBytes()
				);
				
				//get a handle to the join sheet
				java.io.InputStream jsheet = new java.io.ByteArrayInputStream(
					com.rohanclan.ashpool.libxslt.CompiledSheets.sheetDoJoin.getBytes() 
				);
				
				//long distime = 
				bXSL.transform(fakexml, jsheet, output);
				
				//now set the returned file to be to the new master file
				tableman.getTableFile(tmptablename, TableManager.TYPE_TABLE).delete();
				tmptablename = dFile;
				
				//cleanup
				output.flush();
				output.close();
			}
		}
		
		java.io.BufferedInputStream is = new java.io.BufferedInputStream(
			tableman.getTableInputStream(tmptablename,TableManager.TYPE_TABLE)
		);
		//java.io.InputStream is = tableman.getTableInputStream(tmptablename,TableManager.TYPE_TABLE);
		
		InputSource inputSource = new InputSource(is);
		
		//resultset build timer
		//long ttime = System.currentTimeMillis();
		
		AResultSet schema = new AResultSet();
		getTableColumns(tf.getTableName(), schema);
		
		//run the results through the XML->ResultSet
		xmlfilter = new XMLtoResultSetFilter(reader, ars, schema);
		
		xmlfilter.parse(inputSource);
		
		//System.out.println("** XML->ResultSet Took: " + (System.currentTimeMillis() - ttime) + "ms");
		
		is.close();
		xmlfilter.nullify();
		xmlfilter = null;
		tableman.doDropTable(tmptablename);
	}
	
	public String createXPath(String sql) throws Exception {
		return this.createXPath(sql,SelectFilter.FORRESULTSET);
	}
	
	/** try to convert an sql select statement into an xslt stylesheet */
	public String createXPath(String sql, byte type) throws Exception {
		//StringTokenizer stok = new StringTokenizer(sql," ");
		
		//this is kind of kludgy. If wherefilter is null then this is prolly
		//the first call, so make all our needed filters. (hopefully this
		//will speed things up a bit)
		if(wf == null){
			wf  = new WhereFilter(tableman, comman);
			cf  = new ColumnFilter(tableman, comman);
			of  = new OrderFilter(tableman, comman);
			tf  = new TableFilter(tableman, comman);
			jf  = new JoinFilter(tableman, comman);
		}
		
		String select[] = new String[]{"",""};
		String from[]   = new String[]{"",""};
				join    = new String[]{"",""};
		String where[]  = new String[]{"",""};
		//String group[] = new String[]{"",""};
		String order[]  = new String[]{"",""};
		
		// 1)
		//save any user defined strings so they dont jack up our splits
		Map<String,String> savedStrings = new HashMap<String,String>();
		sql = Functions.placeHoldStrings(sql,savedStrings);
		
		//the parser expects spaces between some elements, now that
		//strings are removed, adjust the query string
		sql = sql.replaceAll("\\(", " ( ");
		sql = sql.replaceAll("\\)", " ) ");
		sql = sql.replaceAll(",", ", ");
		
		// 2)
		//make sure keywords are lower case and
		//escape subqueries and joins so they dont jack up our splits
		sql = Functions.unCaseKeywords(sql);
		sql = Functions.escapeSubQueries(sql);
		
		// 3)
		// split the query into it's parts
		order = sql.split("order by");
		where = order[0].split("where");
		join  = where[0].split("%ASHPOOLJOIN%");
		from = join[0].split("from");
		select = from[0].split("select");
		
		String whereclause="";
		String fieldclause ="";
		String orderbyclause="";
		
		// 4)
		//The concept is if any of the arrays have an element at a position > 0
		//then they have a part of the query (joins can have many for example)
		if(select.length > 1){
			/* System.out.println("Select: " + 
				Functions.unplaceHoldStrings(select[1],savedStrings)
			);*/
			fieldclause = cf.createXPath(
				Functions.unplaceHoldStrings(select[1],savedStrings)
			);
			doDistinct = cf.getDistinctFlag();
		}
		
		if(from.length > 1){
			//System.out.println("From: " + from[1]);
			of.setTable(tf.createXPath(from[1]));
			mainfile = tf.getTableName();
		}
		
		if(join.length > 1){
			for(int i=1; i<join.length; i++){
				join[i] = Functions.unplaceHoldStrings(join[i],savedStrings);
				//System.out.println("j: " + 
				//	Functions.unplaceHoldStrings(join[i],savedStrings)
				//);
			}
		}
		
		if(where.length > 1){
			//System.out.println("Where: " + where[1]);
			whereclause = wf.createXPath(
				Functions.unplaceHoldStrings(where[1],savedStrings)
			);
		}
		
		if(order.length > 1){
			//System.out.println("Order: " + order[1]);
			orderbyclause = of.createXPath(order[1]);
		}
		
		
		//build the style sheet we are goint to use depending on the type
		//of result the caller is expecting
		switch(type){
			case SelectFilter.FORRESULTSET:
				//if this is not a join
				String newSheet = S_XSLT
					+ whereclause
					+ SF_XSLT
					+ orderbyclause
					+ AS_XSLT
					+ cf.getLimit()
					+ ASL_XSLT
					+ AW_XSLT;
				
				//if they did select *
				if(fieldclause.length() > 0){
					newSheet += fieldclause.toString();
					
				//else build the column list
				}else{
					int fds = cf.getColumnNames().size();
					String valuepart = "";
					for(int i=0; i<fds; i++){
						valuepart = cf.getColumnValues().get(i).toString().replaceAll("\\$ASH_REPLACE_AGG", whereclause).trim();
						
						
						newSheet +=
							xslif_start + cf.getColumnNames().get(i).toString() + xslif_close
							+ element_start + cf.getColumnNames().get(i).toString() + element_close
							+ value_of_start + valuepart + value_of_end
							+ element_end
							+ xslif_end;
							/*B_COL + cf.getColumnNames().get(i).toString()
							+ S_COL + valuepart.replaceAll("['\\( \\)\\/+\\-\\*,\\[\\]]","")
							+ O_COL //+ valuepart.replaceAll("'","")
							+ T_COL + valuepart.replaceAll("['\\( \\)\\/+\\-\\*,\\[\\]]","")
							+ C_COL + valuepart
							+ E_COL;*/
					}
				}
				
				newSheet += AL_XSLT;
				return newSheet;
				
				//}else{
					/*if(fieldclause.length() > 0){
						System.out.println(fieldclause.toString());
						
					//else build the column list
					}else{
						int fds = fieldDis.size();
						for(int i=0; i<fds; i++){
							System.out.println(fieldDis.get(i).toString());
							System.out.println(fieldVal.get(i).toString().replaceAll("\\$ASH_REPLACE_AGG", whereclause.toString()));
						}
					}
					
					System.out.println(whereclause.toString());
					
					int q = joinfromc.size();
					for(int x=0; x<q; x++){
						System.out.println(joinfromc.get(x).toString());
						System.out.println(jointable.get(x).toString());
						System.out.println(jointoc.get(x).toString());
					}*/
					
					//this will change to the new join way... :)
					//return ""; //S_XSLT 
					//+ whereclause.toString()
					//+ SF_XSLT 
					//+ orderbyclause.toString() 
					//+ AS_XSLT 
					//+ AW_XSLT_JOIN + fieldclause.toString()
					//+ AL_XSLT_JOIN + joinfromc.get(0).toString()
					//+ AL_XSLT_JOINC + jointable.get(0).toString()
					//+ AL_XSLT_JOINT + jointoc.get(0).toString()
					//+ AL_XSLT_JOINF + whereclause.toString()
					//+ AL_XSLT_JOINF2 + orderbyclause.toString()
					//+ AL_XSLT_JOINFS + fieldclause.toString()
					//+ AL_XSLT_JOINFL;
				//}
				
			case SelectFilter.FORSINGLE:
				//the result of this should only be one text node value from 
				//the xmldoc... but if many are returned it becomes a list
				//that could jack up text lists
				return S_XSLT1
					+ whereclause.toString()
					+ SF_XSLT1 
					+ orderbyclause.toString() 
					+ AS_XSLT1 + cf.getLimit()
					+ AW_XSLT1 + cf.getColumnValues().get(0).toString()
					+ AL_XSLT1;
		}
		
		return null;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/** the basics of the xslt stylesheet that will make an xml docuemnt
	 * that can go straight to XMLtoResultSetFilter to make a result set
	 */
	private static final String S_XSLT  ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
	+ "<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\""
	+ " xmlns:math=\"http://exslt.org/math\""
	+ " xmlns:set=\"http://exslt.org/sets\""
	+ " xmlns:date=\"http://exslt.org/dates-and-times\""
	+ " xmlns:exslt=\"http://exslt.org/common\""
	+ " xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
	+ " extension-element-prefixes=\"math set date exslt\""
	+ ">"
	+ "<xsl:output method=\"xml\" indent=\"no\" encoding=\"utf-8\"/>"
		+ "<xsl:param name=\"datastore\" select=\"'file:///'\"/>"
		
	+ "<xsl:template match=\"/\">"
		+ "<t>"
				+ "<xsl:apply-templates select=\"*/*"; /* [clientid = 3 or firstname = 'Steve'] */
	private static final String SF_XSLT = "\">";
				/* sort items go here */
	private static final String AS_XSLT= "</xsl:apply-templates>"
		+ "</t>"
	+ "</xsl:template>"
	+ "<xsl:template match=\"*/*\">"
	+ "<xsl:variable name=\"pos\" select=\"position()\" />"
	+ "<xsl:if test=\". != '' ";
	private static final String ASL_XSLT = "\">" ;
	private static final String AW_XSLT = "<r>"; //"\">"
		 
		//+ "<xsl:copy-of select=\""; /* [name() = 'firstname' or name() = 'lastname'] */
	private static final String AL_XSLT = //"\"/>"
				//add other columns
		"</r></xsl:if>"
	+ "</xsl:template>"
	+ "<xsl:template match=\"text()\"/>"
	+ "</xsl:stylesheet>";
	///////////////////////////////////////////////////////////////////////////
	
	//<xsl:element name="id">
	//private static final String B_COL = "<xsl:element name=\""; //AS part
	//private static final String S_COL = "\">"
		//+ "<xsl:choose>"
		//+ "<xsl:when test=\"not("; // value part (id)
		//+ "<xsl:when test=\""; // value part (id)
	
	//private static final String O_COL = " instance of xs:string) and "; //value part (id)
	//private static final String O_COL = "/*[1] instance of element() "; //value part (id)
	
	//private static final String T_COL = "\">"
	//private static final String T_COL = "/*[name() != '']\">"
		//+ "<xsl:copy-of select=\""; //value part (id)
	//private static final String C_COL = "/*\"/>"
		//+ "</xsl:when>"
		//+ "<xsl:otherwise>"
		//	 + "<xsl:value-of select=\""; //value part
	//private static final String E_COL = "\"/>"
		//+ "</xsl:otherwise>"
		//+ "</xsl:choose>"
	//+ "</xsl:element>";
	
	//<xsl:copy-of select=\"";
	//private static final String E_COL = "\"/></xsl:element>";
	
	
	///////////////////////////////////////////////////////////////////////////
	//private static final String AW_XSLT_JOIN =
	//            "<xsl:variable name=\"table1\" select=\"./"; /* [name() = 'firstname' or name() = 'lastname'] */
				
	//private static final String AL_XSLT_JOIN ="\"/>"
		
	//	+ "<xsl:variable name=\"join1\" select=\""; /* 'join from column' */
				
	//private static final String AL_XSLT_JOINC ="\"/>"
	//		+ "<xsl:variable name=\"table2\" select=\"document(concat($datastore,'/','"; /* join to table */
				
	//private static final String AL_XSLT_JOINT =".xml'))/*/*["; /* join to column */
	
	//private static final String AL_XSLT_JOINF = " = $join1]"; /* limit the selected table name = 'blarg' (where) */
	
	//private static final String AL_XSLT_JOINF2 = "\"/>"
	//	+ "<xsl:for-each select=\"$table2\">";
	
	//private static final String AL_XSLT_JOINFS =
	  //            "<r>"
	//		+ "<xsl:copy-of select=\"$table1\"/>"
	//		+ "<xsl:variable name=\"pos\" select=\"position()\"/>"
	//		+ "<xsl:copy-of select=\"$table2[$pos]/"; /* limit join columns [name()=] */
						
	// private static final String AL_XSLT_JOINFL = "\"/>"
	//	+ "</r>"
		//        + "</xsl:for-each>"
	
	//+ "</xsl:template>"
	
	//+ "<xsl:template match=\"text()\"/>"
	//+ "</xsl:stylesheet>";
	
	//////////////////////////////////////////////////////////////////////////////
	//I know this is crazy duplicated!
	//the result of this should only be one
	//text node value from the xmldoc
	private static final String S_XSLT1  ="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
	+ "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
	+ "<xsl:output method=\"text\" indent=\"no\" encoding=\"utf-8\"/>"
	+ "<xsl:template match=\"/\">"
				+ "<xsl:apply-templates select=\"*/*"; /* [clientid = 3 or firstname = 'Steve'] */
	private static final String SF_XSLT1 = "\">";
				/* sort items go here */
	private static final String AS_XSLT1 = "</xsl:apply-templates>"
	+ "</xsl:template>"
	
	+ "<xsl:template match=\"*/*\">" 
	+ "<xsl:variable name=\"pos\" select=\"position()\" />"
	+ "<xsl:if test=\". != '' ";
	private static final String AW_XSLT1 = "\">" +
		"<xsl:value-of select=\""; /* [name() = 'firstname' or name() = 'lastname'] */
	private static final String AL_XSLT1 = "\"/>,"
	+ "</xsl:if></xsl:template>"
	+ "<xsl:template match=\"text()\"/>"
	+ "</xsl:stylesheet>";
	
	///////////////////////////////////////////////////////////////////////////
	/** the basics of a sort */
	//private static final String S_sort ="<xsl:sort select=\"";
	//private static final String O_sort ="\" order=\"";
	//private static final String D_sort ="\" data-type=\"";
	//private static final String E_sort ="\"/>";
	
	// xsl to create a new element
	private static final String element_start = "<xsl:element name=\"";
	private static final String element_close = "\">";
	private static final String element_end = "</xsl:element>";
	
	// xls to get the value
	private static final String value_of_start = "<xsl:value-of select=\"";
	private static final String value_of_end = "\"/>";
	
	// xsl:if
	private static final String xslif_start = "<xsl:if test=\"//";
	private static final String xslif_close = "\">";
	private static final String xslif_end = "</xsl:if>";
}
