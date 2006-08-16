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
 * XMLtoResultSetFilter.java
 *
 * Created on February 2, 2003, 8:45 AM
 */

package com.rohanclan.ashpool.core.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.rohanclan.ashpool.core.AResultSet;
import com.rohanclan.ashpool.core.AResultSetMetaData;
import com.rohanclan.ashpool.core.ResultColumn;

/**
 * Takes an XML Document in the format
 * &lt;name&gt;
 *      &lt;name&gt;
 *          &lt;fieldname&gt;&lt;/fieldname&gt;
 *          &lt;fieldname&gt;&lt;/fieldname&gt;
 *      &lt;/name&gt;
 *      &lt;name&gt;
 *          &lt;fieldname&gt;&lt;/fieldname&gt;
 *          &lt;fieldname&gt;&lt;/fieldname&gt;
 *      &lt;/name&gt;
 *&lt;/name&gt;
 * and turns it into a AResultSet object which can be used by most sql programs
 * as results from a database query
 * @author Rob Rohan
 */
public class XMLtoResultSetFilter extends org.xml.sax.helpers.XMLFilterImpl {
	
	private AResultSet ars;
	private AResultSet schema;
	
	private String currentElement="";
	
	private String rowmarker="";
	private String tablemarker="";
	
	private int currentrow=-1;
	
	private StringBuffer ResValue;
	
	/** Creates a new instance of XMLtoResultSetFilter */
	public XMLtoResultSetFilter(XMLReader reader) {
		super(reader);
		ars = new AResultSet();
		ResValue = new StringBuffer();
	}
	
	/** create the object with the desired reader and a pointer to a recordset */
	public XMLtoResultSetFilter(XMLReader reader, AResultSet rs){
		super(reader);
		ResValue = new StringBuffer();
		ars = rs;
		schema = null;
	}
	
	public XMLtoResultSetFilter(XMLReader reader, AResultSet rs, AResultSet schema){
		super(reader);
		ResValue = new StringBuffer();
		ars = rs;
		this.schema = schema;
	}
	
	public int getColumnType(String columnname){
		int type = java.sql.Types.VARCHAR;
		
		try{
			if(schema != null 
				&& ((AResultSetMetaData)schema.getMetaData()).getRecordCount() > 0){
				while(schema.next()){
					if(schema.getString("COLUMN_NAME").equals(columnname)){
						type = Integer.parseInt(schema.getString("DATA_TYPE"));
						//reset the result set
						schema.beforeFirst();
						break;
					}
				}
			}
		}catch(java.sql.SQLException sqle){
			//do nothing
		}
		
		return type;
	}
	
	
	/** get the results of the object parse */
	public AResultSet getResultSet(){
		return this.ars;
	}
	
	public void startDocument() throws SAXException{
		ars.reset();
	}
	
	/** handle start elements */
	public void startElement(String uri, String localName, String qName, Attributes attr) 
		throws SAXException{
		//System.out.println(localName);
		try{
			//the start of the result set should be wrapped in something
			if(tablemarker.length() == 0){
				tablemarker = qName;
			//if there is no row marker this is the first pass, into the table
			//so assume this first element is the rowmarker, or if the qName
			//is the same as the rowmarker assume this should be a new row
			}else if(rowmarker.length() == 0 || rowmarker.equals(qName)){
				//System.out.println("setting rowmarker " + qName);
				rowmarker = qName;
				currentrow++;
			//if we are in a row and the current element is empty focus on this node
			}else if(currentElement.length() == 0){
				//System.out.println("in a row focusing on " + qName);
				currentElement = qName;
				
				//if this name is not in the result set column, add it
				//System.out.println("column " + localName + " exists? " + ars.columnExists(localName));
				if(!ars.columnExists(qName)){
					//System.out.println("building a column for " + localName);
					ResultColumn rc = new ResultColumn();
					//rc.type = java.sql.Types.VARCHAR;
					rc.setType(getColumnType(qName));
					rc.columnName = currentElement;
					ars.addResultColumn(rc);
				//if this column does exist in the result set column
				//add a new field
				}else{
					
				} 
			//if there is something in the current element and it is not this 
			//element, then it must be a sub element, so add the fragment
			}else if(!currentElement.equals(qName)){
				ResValue.append("<" + qName + ">");
			}
		}catch(Exception e){
			System.err.println("XML->RS:Start Element: " + e.toString() + " pass# " + currentrow);
			e.printStackTrace(System.err);
		}
	}
	
	/** handle element data */
	public void characters(char ch[], int start, int length) throws SAXException {
		for(int x=start; x<(start+length); x++){
			ResValue.append(ch[x]);
		}
	}
	
	/** handle end of the element */
	public void endElement(String uri, String localName, String qName) throws SAXException{
		try{
			//this is the end of this "field" clean up and start over
			if(qName.equals(currentElement) && !currentElement.equals("")){
				
				//System.out.println("add data to " + localName + " and starting process over");
				//long stime = new java.util.Date().getTime();
				
				//this needs to be faster.
				ResultColumn trc = (ResultColumn)ars.getResultTable().get(
					(ars.findColumn(currentElement) - 1)
				);
				
				//((java.util.List)trc.columnData).add(ResValue.toString().trim());
				trc.columnData.add(ResValue.toString());
				
				/* switch(trc.getType()){
					case java.sql.Types.INTEGER:
					case java.sql.Types.NUMERIC:
						trc.columnData.add(new Integer(ResValue.toString()));
						break;
					case java.sql.Types.VARCHAR:
					case java.sql.Types.CHAR:
					default:
						trc.columnData.add(ResValue.toString());
						break;
				} */
				
				
				//System.out.println("assign took: " + (new java.util.Date().getTime() - stime) + "ms");
				
				
				currentElement="";
				//clear out ResValue. I was just removing the buffer contents
				//but it would cause an out of memory error. I think doing it
				//this way allows the old buffer to be GCed. and fixes the out
				//o-mem error. this is an oddity. I don't konw how to handle this
				//seems faster to delete, but kills memory BLARG!
				//ResValue.delete(0, ResValue.length())
				ResValue = new StringBuffer();
				//System.out.println("Mem: " + Runtime.getRuntime().freeMemory());
				
			//if this is the end of the row
			}else if(qName.equals(rowmarker)){
				//System.out.println("changing rowmarker to null");
				//currentrow++;
				rowmarker="";
			}else{
				ResValue.append("</" + qName + ">");
			}
		}catch(Exception e){
			System.err.println("XML->RS:End Element: " + e.toString() + " pass# " + currentrow);
			e.printStackTrace(System.err);
		}
	}
	
	public void nullify(){
		currentElement=null;
		rowmarker=null;
		tablemarker=null;
		ResValue=null;
	}

}
