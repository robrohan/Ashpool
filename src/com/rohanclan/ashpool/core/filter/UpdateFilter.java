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
 * UpdateFilter.java
 *
 * Created on February 1, 2003, 10:16 AM
 */ 

package com.rohanclan.ashpool.core.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.StringTokenizer;

import com.rohanclan.ashpool.core.AResultSet;
import com.rohanclan.ashpool.core.AshpoolException;
import com.rohanclan.ashpool.core.AshpoolSQLFilter;
import com.rohanclan.ashpool.core.CommandManager;
import com.rohanclan.ashpool.core.Functions;
import com.rohanclan.ashpool.core.TableManager;
import com.rohanclan.ashpool.core.Validator;
import com.rohanclan.ashpool.core.xml.BasicXSLEngine;

/**
 * filter to update fields in a table
 * @author  rob
 */
public class UpdateFilter extends SQLFilter implements AshpoolSQLFilter {

	//private List insertColumnNames;
	//private List insertColumnValues;
	//private java.util.Random rand;
	
	//private Validator val;
	
	private WhereFilter wf;
	private SetFilter sf;
	private TableFilter tf;
	
	
	/** Creates a new instance of UpdateFilter */
	public UpdateFilter(TableManager tman, CommandManager com) {
		super(tman, com);
		//insertColumnNames = new ArrayList();
		//insertColumnValues = new ArrayList();
		//rand = new java.util.Random(System.currentTimeMillis());
		//used to validate fields
		//val = new Validator();
	}
	
	/** sets the xslt engine */
	public void setXSLEngine(BasicXSLEngine bxsl){
		this.bXSL = bxsl;
	}
	
	public void executeQuery(String sql, AResultSet ars) throws Exception {
		//System.out.println(createXPath(sql));
		String tmpfile ="";
		java.io.InputStream is = null;
		java.io.OutputStream os = null;
		 
		synchronized("MODIFY_" + mainfile){
			try{
				//stream for update sheet - needs to run first to set mainfile
				 java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(
					//createXPath(sql, type).getBytes("UTF-8")
					createXPath(sql).getBytes("iso-8859-1")
				);
				
				//get a somewhat unique tmpfile name
				tmpfile = "!utmp" + System.currentTimeMillis();
				tableman.createTable(tmpfile,"", TableManager.TYPE_TABLE);
				
				//get an input stream to the real table
				is = tableman.getTableInputStream(
					mainfile, TableManager.TYPE_TABLE
				);
				
				//create a new main file and get an outputstream
				os = tableman.getTableOutputStream(tmpfile, TableManager.TYPE_TABLE);
				
				//make sure the engine knows the way to the datastore
				bXSL.setParam("datastore", "file://" + tableman.getDatastore().getAbsolutePath());
				
				//apply the stylesheet and get the results
				//long ttime = 
				bXSL.transform(is, bais, os);
				
				//System.out.println("** Update Took: " + ttime + "ms");
				
				os.flush();
				os.close();
				is.close();
				
				//delete the main file and move the new temp file to the main file
				tableman.getTableFile(mainfile, TableManager.TYPE_TABLE).delete();
				tableman.renameTable(tmpfile, mainfile);
				
			}catch(Exception e){
				//try to clean up quite like
				try{
					os.close();
					is.close();
				}catch(Exception x){;}
				try{
					tableman.getTableFile(tmpfile, TableManager.TYPE_TABLE).delete();
				}catch(Exception z){;}
				
				//move the real table back
				//tableman.renameTable(tmpfile, mainfile);
				throw e;
				//throw new java.sql.SQLException("Update Error: " + e.toString());
			}
		}
	}
	
	public String createXPath(String sql) throws Exception {
		/* update customers 
			set fname = 'fresh thing man', 
			lname = 'this', 
			id = 123 
			where x = 12 */
		//StringTokenizer stok = new StringTokenizer(sql," ");
		StringBuffer whereclause = new StringBuffer();
		//String currenttok = "";
		
		List<String> insertColumnNames = new ArrayList<String>();
		List<String> insertColumnValues = new ArrayList<String>();
		
		//this is kind of kludgy. If wherefilter is null then this is prolly
		//the first call, so make all our needed filters. (hopefully this
		//will speed things up a bit)
		if(wf == null){
			wf  = new WhereFilter(tableman, comman);
			//cf  = new ColumnFilter(tableman, comman);
			//of  = new OrderFilter(tableman, comman);
			sf = new SetFilter(tableman, comman);
			tf  = new TableFilter(tableman, comman);
		}
		
		String where[] = new String[]{"",""};
		String set[] = new String[]{"",""};
		String update[] = new String[]{"",""};
		
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
		set = where[0].split("set");
		update = set[0].split("update");
		
		if(where.length > 1){
			//System.out.println("where: " + where[1]);
			whereclause.append(wf.createXPath(
				Functions.unplaceHoldStrings(where[1],savedStrings)
			));
			//System.out.println(whereclause);
		}
		
		if(set.length > 1){
			//System.out.println("set : " + set[1]);
			//make sure the tokens will work out well (expects spaces)
			set[1] = set[1].replaceAll("="," = ");
			set[1] = set[1].replaceAll(","," , ");
			//System.out.println(set[1]);
			//put the proper text back in (so we get good values)
			set[1] = Functions.unplaceHoldStrings(set[1],savedStrings);
			//try to parse out column & values
			sf.getColumnsAndValues(set[1], insertColumnNames, insertColumnValues);
			//System.out.println(insertColumnNames + " " + insertColumnValues);
		}
		
		if(update.length > 1){
			tf.createXPath(update[1]);
			mainfile = tf.getTableName();
			//System.out.println("update: " + mainfile);
		}
		
		////////////////////////////////////////////////////////////////////////////////////
		
		//System.out.println(this.insertColumnNames);
		//System.out.println(this.insertColumnValues);
		//System.out.println(whereclause.toString());
		AResultSet ars = new AResultSet();
		
		//now get a ResultSet of all the columns this table should have
		comman.sf.getTableColumns(mainfile, ars);
	
		//get the row marker and table marker for insert
		String rowmarker = comman.sf.getRowMarker(mainfile);
		String tablemarker = comman.sf.getTableMarker(mainfile);
	
		//loop over all the columns and build the fragment
		//check for data validity as well
		//boolean found=false;
		StringBuffer updateFrag = new StringBuffer();
		
		//WARNING: this assumes xsd and xml columns are defined in the 
		//same order. If "create table" was used then they should be.
		int arssize = ars.getList("COLUMN_NAME").size();
		for(int z=0; z<arssize; z++){
			
			String coltmp = ars.getList("COLUMN_NAME").get(z).toString();
			
			//if this is an auto_number column it can not be updated this way
			//so build a copy type element
			if(ars.getList("AUTO_NUMBER").get(z) != null 
				&& ars.getList("AUTO_NUMBER").get(z).toString().toLowerCase().equals("yes")){
				updateFrag.append(U_ELEN + coltmp + U_ELEEN
					+ U_ELES + coltmp + U_ELEES
					+ U_ELEE
				);
				
				//if they asked to update this column, tell'em that it cant
				if(insertColumnNames.indexOf(coltmp) >= 0){
					//resetNameValue();
					throw new AshpoolException("Can not explicitly update column " 
						+ coltmp + " in table " + mainfile
						+ " because it is marked as type serial. Use Alter if you want to change the serial value."
					);
				}
				
				//end of story.
				continue;
			}
			
			//if this is an update column
			if(insertColumnNames.indexOf(coltmp) >= 0){
				
				if(ars.getList("IS_NULLABLE").get(z).toString().toLowerCase().equals("no")
					&& insertColumnValues.get(insertColumnNames.indexOf(coltmp)).toString().toLowerCase().equals("null")
				){
					//resetNameValue();
					throw new AshpoolException(coltmp + " does not allow null values.");
				}
				
				updateFrag.append(U_ELEN + coltmp + U_ELEEN
					+ Validator.validate(
						insertColumnValues.get(insertColumnNames.indexOf(coltmp)).toString(),
						ars.getList("DATA_TYPE").get(z).toString(),
						ars.getList("COLUMN_SIZE").get(z).toString()
						)
					+ U_ELEE
				);
				
				//remove this from the lists
				insertColumnValues.remove(insertColumnNames.indexOf(coltmp));
				insertColumnNames.remove(insertColumnNames.indexOf(coltmp));
			//this is not an update column just copy the value
			}else{
				updateFrag.append(U_ELEN + coltmp + U_ELEEN
					+ U_ELES + coltmp + U_ELEES
					+ U_ELEE
				);
			}
		}
		
		//make sure we dont jack up the table by adding a column that doesnt exist
		if(insertColumnNames.size() > 0){
			String badNames = insertColumnNames.toString();
			//resetNameValue();
			throw new java.sql.SQLException(
				"The column(s) " + badNames + " - not found in table " + mainfile
				+ ". Update failed."
			);
		}
		
		return US_S + US_ST + tablemarker + US_ST2 
			+ whereclause.toString() + US_STW + rowmarker + US_STR
			+ updateFrag.toString() + US_ETR + rowmarker + US_ESR;
	}
	
	/* private void resetNameValue(){
		//insertColumnNames.clear();
		//insertColumnValues.clear();
	} */
	
	////////////////////////////////////////////////////////////////////////////
	private static final String U_ELEN  = "<xsl:element name=\"";
	private static final String U_ELEEN = "\">";
	private static final String U_ELES  = "<xsl:value-of select=\"";
	private static final String U_ELEES = "\"/>";
	private static final String U_ELEE  = "</xsl:element>";
	////////////////////////////////////////////////////////////////////////////
	
	private static final String US_S = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"
	+ " <xsl:stylesheet version=\"1.0\" "
	+ "        xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\""
	+ "        xmlns:math=\"http://exslt.org/math\""
	+ "        xmlns:set=\"http://exslt.org/sets\""
	+ "        xmlns:date=\"http://exslt.org/dates-and-times\""
	+ "        extension-element-prefixes=\"math set date\""
	+ ">"
	+ "<xsl:output method=\"xml\" indent=\"no\" encoding=\"UTF-8\" />"
	+ "<xsl:param name=\"datastore\" select=\"'file:///'\" />"
	+"        <xsl:template match=\"/\">";
	
	private static final String US_ST = "<xsl:element name=\"";
	private static final String US_ST2 = "\">"
	+ "               <xsl:apply-templates select=\"*/*\" />"
	+ "       </xsl:element>"
	+ "      </xsl:template>"
	
	+ "      <xsl:template match=\"*/*\">"
	+ "               <xsl:choose>"
	+ "                       <xsl:when test=\".";
	private static final String US_STW = "\">"
	+ "                               <xsl:call-template name=\"update\" />"
	+ "                     </xsl:when>"
	+ "                     <xsl:otherwise>"
	+ "                               <xsl:call-template name=\"pass\" />"
	+ "                        </xsl:otherwise>"
	+ "                </xsl:choose>"
	+ "       </xsl:template>"
	
	+ "        <xsl:template name=\"update\">"
	+ "                <xsl:element name=\"";
	
	private static final String US_STR = "\">";
					/** over riding rows here */
	private static final String US_ETR = "</xsl:element>"
	+ "        </xsl:template>"
	+ "        <xsl:template name=\"pass\">"
	
	+ "              <xsl:element name=\"";
	private static final String US_ESR = "\">"
	+ "                      <xsl:copy-of select=\"*\"/>"
	+ "              </xsl:element>"
	+ "        </xsl:template>"
	
	+ "        <xsl:template match=\"text()\"/>"
	+ "</xsl:stylesheet>";
}
