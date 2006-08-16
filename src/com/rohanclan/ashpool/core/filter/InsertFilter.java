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
 * InsertFilter.java
 *
 * Created on February 1, 2003, 10:16 AM
 */

package com.rohanclan.ashpool.core.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.rohanclan.ashpool.core.AResultSet;
import com.rohanclan.ashpool.core.AshpoolSQLFilter;
import com.rohanclan.ashpool.core.CommandManager;
import com.rohanclan.ashpool.core.Functions;
import com.rohanclan.ashpool.core.TableManager;
import com.rohanclan.ashpool.core.Validator;
import com.rohanclan.ashpool.core.xml.BasicXSLEngine;
import com.rohanclan.ashpool.core.xml.XMLInsertFilter;
import com.rohanclan.ashpool.core.xml.XMLWriter;

/**
 * filter to add a row to a table
 * @author  rob
 */
public class InsertFilter extends SQLFilter implements AshpoolSQLFilter {
	private List<String> insertColumnNames;
	private List<String> insertColumnValues;
	
	//private Validator val;
	
	/** Creates a new instance of InsertFilter */
	public InsertFilter(TableManager tman, CommandManager com) {
		super(tman, com);
		insertColumnNames = new ArrayList<String>();
		insertColumnValues = new ArrayList<String>();
		//used to validate fields
		//val = new Validator();
	}
	
	public void setXSLEngine(BasicXSLEngine bxsl){;}
	public String createXPath(String sql) throws Exception{ return null; }
	
	public void executeQuery(String sql, AResultSet ars) throws Exception{
		//if something bad happend on the last insert try to clean up
		if(insertColumnNames.size() > 0 || insertColumnValues.size() > 0){
			cleanup();
		}
		
		//System.out.println(sql);
		////////////////////////////////////////////////////////////////////
		Map<String,String> savedStrings = new HashMap<String,String>();
		sql = Functions.placeHoldStrings(sql,savedStrings);
		//the parser expects spaces between some elements, now that
		//strings are removed, adjust the query string
		sql = sql.replaceAll("\\(", " ( ");
		sql = sql.replaceAll("\\)", " ) ");
		sql = sql.replaceAll(",", ", ");
		sql = Functions.unplaceHoldStrings(sql,savedStrings);
		////////////////////////////////////////////////////////////////////
		
		StringTokenizer stok = new StringTokenizer(sql," ");
		
		//insert
		stok.nextElement();
		//into
		stok.nextElement();
		
		mainfile = stok.nextElement().toString();
		
		//Ashpool specific - dump the next resultset into the file
		if(mainfile.startsWith("~") && !tableman.isTable(mainfile)){
			StringBuffer subSelect = new StringBuffer();
			
			//get the sub select
			while(stok.hasMoreElements()){
				subSelect.append(stok.nextElement().toString() + " ");
			}
			
			if(subSelect.toString().toLowerCase().startsWith("select")){
				//pass the select part to the select filter and get the temp
				//table name
				String tmpname = 
					comman.getSelectFilter().executeQuery(
						subSelect.toString(),SelectFilter.FORRESULTSET
				);
				
				//since this is special, just move the table to the new name
				tableman.renameTable(tmpname, mainfile);
				//tableman.doDropTable(tmpname);
			}else{
				tableman.createTable(mainfile,subSelect.toString(), TableManager.TYPE_TABLE);
			}
			//System.out.println("Insert.");
		//normal sql insert statement
		}else{
			//make sure this is a valid table
			if(!tableman.isTable(mainfile)){
				cleanup();
				throw new java.sql.SQLException("'" + mainfile + "' is not a valid table");
			}
			
			/* insert into test_tbl (
			 *      field,
			 *      field,
			 *      field
			 * ) values (
			 *      1,
			 *      'hello',
			 *      2003-12-12
			 * );
			 */
			//go through the statement and get out the columns and 
			//the values to be added to the table
			String field = stok.nextElement().toString();
			while(!field.equals(")")){
				if(field.endsWith(",")){
					insertColumnNames.add(field.substring(0,field.length() - 1));
				}else if(!field.equals("(") && !field.equals(",")){
					insertColumnNames.add(field); 
				}
				field = stok.nextElement().toString();
			}
			
			//values
			field = stok.nextElement().toString();
			// (
			field = stok.nextElement().toString();
			
			field = stok.nextElement().toString();
			
			while(!field.equals(")")){
				
				if(field.startsWith("'") 
					&& (field.trim().endsWith("'") || field.trim().endsWith("',"))
					&& !field.equals("'")
					){
					if(field.endsWith("',")){
						insertColumnValues.add(field.substring(0,field.length() - 1));
					}else{
						insertColumnValues.add(field);
					}
					
				}else if(field.startsWith("'") || field.equals("'")){
					StringBuffer insline = new StringBuffer();
					insline.append(field);
					field = stok.nextElement().toString();
					
					if(!(field.trim().endsWith("'") || field.trim().endsWith("',")) ){
						while(!field.trim().endsWith("'") && !field.trim().endsWith("',")){
							insline.append(" " + field);
							if(stok.hasMoreElements()){
								field = stok.nextElement().toString();
							}else{
								break;
							}
						}
						
						if(field.endsWith(",")){
							insline.append(" " + field.substring(0,field.length() - 1));
							insertColumnValues.add(insline.toString().trim());
						}else{
							insline.append(" " + field);
							insertColumnValues.add(insline.toString().trim());
						}
						
					//}else{
					//    insline.append(" " + field.substring(0,field.length() - 1));
					//    insertColumnValues.add(insline.toString().trim());
					//}
					}else if(field.endsWith(",")){
						insline.append(" " + field.substring(0,field.length() - 1));
						insertColumnValues.add(insline.toString().trim());
					}else{
						insline.append(" " + field);
						insertColumnValues.add(insline.toString().trim());
					}
				}else if(field.equals(",")){
					 //do nothing
				}else if(field.endsWith(",")){
					insertColumnValues.add(field.substring(0,field.length() - 1));
				}else if(field.equals("(")){
					try{
						String tmpq = "";
						StringBuffer subq = new StringBuffer();
						while(!tmpq.equals(")")){
							subq.append(" " + tmpq);
							tmpq = stok.nextElement().toString();
						}
						//is this really a subquery or a jacked function call?
						if(subq.toString().length() > 6){
							insertColumnValues.add(comman.sf.executeQuery(
								subq.toString(),SelectFilter.FORSINGLE).toString().trim()
							);
						}
					}catch(Exception e){
						System.err.println("Subquery failed " + e.toString());
						//e.printStackTrace(System.err);
					}
				}else{
					insertColumnValues.add(field);
				}
				
				field = stok.nextElement().toString();
			}
			
			//now get a ResultSet of all the columns this table should have
			comman.sf.getTableColumns(mainfile, ars);
			
			//get the row marker and table marker for insert
			String rowmarker = comman.sf.getRowMarker(mainfile);
			String tablemarker = comman.sf.getTableMarker(mainfile);
			
			//the insert string, the row document fragment
			StringBuffer insString = new StringBuffer();
			
			insString.append("<" + rowmarker + ">");
			//loop over all the columns and build the fragment
			//check for data validity as well
			boolean found=false;
			int arssize = ars.getList("COLUMN_NAME").size();
			for(int z=0; z<arssize; z++){
				String coltmp = ars.getList("COLUMN_NAME").get(z).toString();
				
				found=false;
				
				//if they added this column add the data
				//TODO: addin data checking and such
				for(int k=0; k<insertColumnNames.size(); k++){
					//if this column is equal to what they specified and it
					//is not an autonumber column, add it
					if(insertColumnNames.get(k).toString().equals(coltmp)
						&& !(ars.getList("AUTO_NUMBER").get(z) != null 
						&& ars.getList("AUTO_NUMBER").get(z).toString().toLowerCase().equals("yes"))
						){
						
						try{
							insString.append( 
								"<" + coltmp + ">" 
								+ Validator.validate(
									insertColumnValues.get(k).toString(), 
									ars.getList("DATA_TYPE").get(z).toString(),
									ars.getList("COLUMN_SIZE").get(z).toString()
								)
								+ "</" + coltmp + ">"
							); 
						}catch(java.sql.SQLException e){
							//clean up and pass the error along
							insertColumnNames.clear();
							insertColumnValues.clear();
							throw new java.sql.SQLException(e.toString());
						}
						
						insertColumnNames.remove(k);
						insertColumnValues.remove(k);
						found=true;
					}
				}
				
				//else do default stuff
				if(!found){
					//first try auto number
					if(ars.getList("AUTO_NUMBER").get(z) != null
						&& ars.getList("AUTO_NUMBER").get(z).toString().toLowerCase().equals("yes")){
						
						//get the next sequence
						int nextnum = tableman.getNextSequence(mainfile);
						//add it to the fragment
						insString.append(
							"<" + coltmp + ">" 
							+ nextnum
							+ "</" + coltmp + ">"
						);
						//let the command manager know what this id was
						//this should be just variable, but because some DBMS
						//send statements one after another the local scope is lost
						//Hopefully if this is used in a multi-user system whatever
						//sends the SQL statements doesnt loose the local scope
						comman.setVariable("identity",new Integer(nextnum));
						comman.setGlobalVariable("identity",new Integer(nextnum));
						
					//if there is a default value specified use that
					}else if(ars.getList("COLUMN_DEF").get(z) != null 
						&& ars.getList("COLUMN_DEF").get(z).toString().length() > 0){
							
						insString.append(
							"<" + coltmp + ">"
							+ ars.getList("COLUMN_DEF").get(z).toString() 
							+ "</" + coltmp + ">"
						);
					}else{
						//make sure this column can be null first
						if(ars.getList("IS_NULLABLE").get(z).toString().toLowerCase().equals("no")){
							cleanup();
							throw new java.sql.SQLException(coltmp + " does not allow null values");
						}
						insString.append("<" + coltmp + "/>");
					}
				}
			}
			insString.append("</" + rowmarker + ">");
			
			//CRITICAL! (Table lock yikes :)
			//After thinking through this a bit more, I might be able to find
			//the bottom of the file (filelength - tablemarker.getBytes.length)
			//to make inserts faster - TODO i guess. (kills crypto that way tho)
			synchronized("MODIFY_" + mainfile){
				//get a somewhat unique tmpfile name
				String tmpfile = "!itmp" + System.currentTimeMillis();
				
				//move the real file to the temp file
				//tableman.renameTable(mainfile, tmpfile);
				
				//make an empty temp file
				tableman.createTable(tmpfile,"", TableManager.TYPE_TABLE);
				
				//get an input stream to the real table
				java.io.InputStream is = tableman.getTableInputStream(
					mainfile, TableManager.TYPE_TABLE
				);
				
				try{
					//get a reader
					XMLReader reader = XMLReaderFactory.createXMLReader();
					
					//create an XML insert filter to create a new document
					XMLInsertFilter xif = new XMLInsertFilter(
						reader, tablemarker, insString.toString()
					);
					
					//get the writer to write it to the original file
					//(these both use SAX btw)
					java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(
						tableman.getTableOutputStream(tmpfile,TableManager.TYPE_TABLE)
					);
					
					XMLWriter writer = new XMLWriter(
						xif, osw
					);
					
					InputSource inputsource = new InputSource();
					inputsource.setByteStream(is);
					
					writer.parse(inputsource);
					
					osw.flush();
					osw.close();
					is.close();
					
					//now move the temp file to the real filename (commit)
					tableman.getTableFile(mainfile,TableManager.TYPE_TABLE).delete();
					tableman.renameTable(tmpfile, mainfile);
					
				}catch(Exception e){
					//move the table back
					//tableman.renameTable(tmpfile, mainfile);
					
					//remove the tempfile
					tableman.getTableFile(tmpfile, TableManager.TYPE_TABLE).delete();
					
					cleanup();
					ars.reset();
					throw new java.sql.SQLException("Insert failed: " + e.toString());
				}
				
				//tableman.doDropTable(tmpfile);
				//reset everything for next run
				cleanup();
				ars.reset();
				
			}
		}
	}
	
	private void cleanup(){
		insertColumnNames.clear();
		insertColumnValues.clear();
	}

}
