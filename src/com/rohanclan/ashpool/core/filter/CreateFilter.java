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
 * CreateFilter.java
 *
 * Created on February 7, 2003, 8:27 PM
 */

package com.rohanclan.ashpool.core.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.rohanclan.ashpool.core.AResultSet;
import com.rohanclan.ashpool.core.AshpoolException;
import com.rohanclan.ashpool.core.AshpoolSQLFilter;
import com.rohanclan.ashpool.core.CommandManager;
import com.rohanclan.ashpool.core.TableManager;
import com.rohanclan.ashpool.core.xml.BasicXSLEngine;

/**
 * filter to create tables
 * @author  rob
 */
public class CreateFilter extends SQLFilter implements AshpoolSQLFilter {
	private static final String XSDNS = "xs:";
	
	protected static List<String> int_type;
	protected static List<String> float_type;
	protected static List<String> string_type;
	protected static List<String> boolean_type;
	protected static List<String> date_type;
	protected static List<String> double_type;
	protected static List<String> decimal_type;
	protected static List<String> datetime_type;
	
	//private AResultSet rs;
	
	/** Creates a new instance of CreateFilter */
	public CreateFilter(TableManager tman, CommandManager com) {
		super(tman, com);
		//rs = new AResultSet();
		buildTypeLists();
	}
	
	private void buildTypeLists(){
		if(int_type == null){
			int_type = new ArrayList<String>();
			float_type = new ArrayList<String>();
			string_type = new ArrayList<String>();
			boolean_type = new ArrayList<String>();
			date_type = new ArrayList<String>();
			double_type = new ArrayList<String>();
			decimal_type = new ArrayList<String>();
			datetime_type = new ArrayList<String>();
			
			//int mapping
			int_type.add("integer");
			int_type.add("int8");
			int_type.add("int4");
			int_type.add("int");
			//float mapping
			float_type.add("float");
			float_type.add("float8");
			float_type.add("money");
			float_type.add("currency");
			float_type.add("float4");
			//string mapping
			string_type.add("string");
			string_type.add("varchar");
			string_type.add("nvarchar");
			string_type.add("char");
			//boolean mapping
			boolean_type.add("boolean");
			boolean_type.add("bool");
			boolean_type.add("flag");
			boolean_type.add("yes/no");
			//date
			date_type.add("date");
			//double
			double_type.add("double");
			//decimal
			decimal_type.add("decimal");
			//date time
			datetime_type.add("dateTime");
			datetime_type.add("timestamp");
			
		}
	}
	
	
	/** sets the table manager */
	public void setTableManager(TableManager tman){
		tableman = tman;
	}
	
	/** set the xslt engine */
	public void setXSLEngine(BasicXSLEngine bxsl){
		this.bXSL = bxsl;
	}
	
	public String createXPath(String sql){
		return null;
	}
	
	/** will look like
	 * create table mytable ( id int, firstname string, date datetime )
	 */
	public void executeQuery(String sql, AResultSet rs) throws Exception{
		boolean needserial = false;
		
		//the parser expects spaces between some elements, now that
		//strings are removed, adjust the query string
		sql = sql.replaceAll("\\(", " ( ");
		sql = sql.replaceAll("\\)", " ) ");
		sql = sql.replaceAll(",", ", ");
		
		StringTokenizer stok = new StringTokenizer(sql," ");
		
		//create
		stok.nextElement();
		//table
		stok.nextElement();
		//tablename
		String newfilename = stok.nextElement().toString();
		
		//the opening (
		stok.nextElement();
		
		StringBuffer columndefs = new StringBuffer();
		
		String primarykey = "";
		
		while(stok.hasMoreElements()){
			String name = stok.nextToken();
			
			if(name.equals(",")){
				name = stok.nextElement().toString();
			}
			
			//the end of the command
			if(name.equals(")")){ break; }
			
			String type = stok.nextToken();
			
			//System.err.println(type);
			
			String size = "";
			String nullable = "true";
			//if there is no comma assume there is more to be said
			//about this column
			if(type.indexOf(",") <= 0){
				//often a ( or not or primary
				String constraint = stok.nextToken();
				
				//this could be the last element
				if(!constraint.equals(")")){
					
					//should look like - varchar ( 300 )
					if(constraint.equals("(")){
						//300 in our example
						size = stok.nextToken();
						//should now be )
						String pacomma = stok.nextToken();
						
						if(!pacomma.trim().endsWith(",")){
							constraint = stok.nextToken();
						}else{
							constraint = pacomma;
						}
					}
					
					//could be more info could be a comma
					//constraint = stok.nextToken();
					
					//still more to be said?
					//if(!constraint.equals(")")){
					
					if(!constraint.trim().equals(",") && !constraint.trim().equals(")")){
						//constraint = stok.nextToken();
						
						if(constraint.toLowerCase().equals("primary")){
							if(primarykey.length() < 1){
								primarykey = "true";
							}else{
								throw new java.sql.SQLException("There can be only one primary key per table");
							}
							//assume the word key is the next word
							constraint = stok.nextToken();
						}
						
						if(constraint.toLowerCase().equals("null")){
							nullable = "true";
							//assume "null" is the next word
							//constraint = stok.nextToken();
						}
						
						if(constraint.toLowerCase().equals("not")){
							nullable = "false";
							//assume "null" is the next word
							constraint = stok.nextToken();
						}
					}
				}
			}
			
			//if there is a comma remove it
			if(type.indexOf(",") > 0)
				type = type.substring(0,type.length() -1);
			
			//this is for the future, pk's and auto numbers and such
			//if the type doesn't have a , - for now though it's just the
			//last column
			columndefs.append(S_ELE + name);
			
			if(type.toLowerCase().equals("serial") && !needserial){
				columndefs.append(S_TYP + XSDNS + "integer");
				columndefs.append(S_NIL + "false");
				columndefs.append(S_AUT + "true");
				needserial = true;
			}else if(type.toLowerCase().equals("serial") && needserial){
				throw new java.sql.SQLException("Sorry, tables can only have one serial column");
				
			}else if(string_type.contains(type.toLowerCase())){
				//varchar and char are standard
				columndefs.append(S_TYP + XSDNS + "string");
				columndefs.append(S_NIL + nullable);
				
			}else if(int_type.contains(type.toLowerCase())){
				columndefs.append(S_TYP + XSDNS + "integer");
				columndefs.append(S_NIL + nullable);
				
			}else if(float_type.contains(type.toLowerCase())){
				columndefs.append(S_TYP + XSDNS + "float");
				columndefs.append(S_NIL + nullable);
			
			}else if(boolean_type.contains(type.toLowerCase())){
				columndefs.append(S_TYP + XSDNS + "boolean");
				columndefs.append(S_NIL + nullable);
				
			}else if(double_type.contains(type.toLowerCase())){
				columndefs.append(S_TYP + XSDNS + "double");
				columndefs.append(S_NIL + nullable);
			
			}else if(date_type.contains(type.toLowerCase())){
				columndefs.append(S_TYP + XSDNS + "date");
				columndefs.append(S_NIL + nullable);
				
			}else if(decimal_type.contains(type.toLowerCase())){
				columndefs.append(S_TYP + XSDNS + "decimal");
				columndefs.append(S_NIL + nullable);
				
			}else if(datetime_type.contains(type.toLowerCase())){
				columndefs.append(S_TYP + XSDNS + "dateTime");
				columndefs.append(S_NIL + nullable);
			
			//if we dont check the type, assume its ok (this should be an
			//error at some point)
			}else{
				//throw new AshpoolException("?" + S_TYP + XSDNS + type + "::" + S_NIL + nullable);
				throw new AshpoolException("? ]" + name + "[ ]" + type + "[ ]" + nullable + "[  " + sql);
				//columndefs.append(S_TYP + XSDNS + type);
				//columndefs.append(S_NIL + nullable);
			}
			
			if(size.length() > 0)
				columndefs.append(S_MAX + size.trim());
			
			columndefs.append(E_ELE);
		}
		
		//create the schema
		tableman.createTable(
			newfilename, S_XSD + columndefs.toString() + E_XSD, TableManager.TYPE_SCHEMA
		);
		//create the table
		tableman.createTable(
			newfilename, S_XML +  E_XML, TableManager.TYPE_TABLE
		);
		//create an autonumber tracking table if needed
		if(needserial)
			tableman.createTable(newfilename, "\u0000\u0000\u0000\u0000", TableManager.TYPE_COUNTER);
		
		//rs.setQuickResultSet("Create Table", "Create.");
		//System.out.println("Create.");
		
		columndefs = null;
	}
	
	/** returns the currently supported data types */
	public void getSupportedDataTypes(AResultSet rs){
		
		List<Object> vNull = new ArrayList<Object>();
		List<Object> vName = new ArrayList<Object>();
		List<Object> vType = new ArrayList<Object>();
		List<Object> vPres = new ArrayList<Object>();
		List<Object> vSearch = new ArrayList<Object>();
		List<Object> vAuto = new ArrayList<Object>();
		
		//////////////////////////////////////////
		vNull.add("");
		vName.add("serial");
		vType.add(new Integer(java.sql.Types.INTEGER));
		vPres.add(new Integer(0));
		vSearch.add(new Boolean(true));
		vAuto.add(new Boolean(true));
		//////////////////////////////////////////
		vNull.add("");
		vName.add("integer");
		vType.add(new Integer(java.sql.Types.INTEGER));
		vPres.add(new Integer(0));
		vSearch.add(new Boolean(true));
		vAuto.add(new Boolean(false));
		//////////////////////////////////////////
		vNull.add("");
		vName.add("decimal");
		vType.add(new Integer(java.sql.Types.DECIMAL));
		vPres.add(new Integer(0));
		vSearch.add(new Boolean(true));
		vAuto.add(new Boolean(false));
		//////////////////////////////////////////
		vNull.add("");
		vName.add("float");
		vType.add(new Integer(java.sql.Types.FLOAT));
		vPres.add(new Integer(0));
		vSearch.add(new Boolean(true));
		vAuto.add(new Boolean(false));
		//////////////////////////////////////////
		vNull.add("");
		vName.add("double");
		vType.add(new Integer(java.sql.Types.DOUBLE));
		vPres.add(new Integer(0));
		vSearch.add(new Boolean(true));
		vAuto.add(new Boolean(false));
		//////////////////////////////////////////
		vNull.add("");
		vName.add("string");
		//vType.add(new Integer(java.sql.Types.VARCHAR));
		vType.add(new Integer(java.sql.Types.VARCHAR));
		vPres.add(new Integer(0));
		vSearch.add(new Boolean(true));
		vAuto.add(new Boolean(false));
		//////////////////////////////////////////
		vNull.add("");
		vName.add("date");
		vType.add(new Integer(java.sql.Types.DATE));
		vPres.add(new Integer(0));
		vSearch.add(new Boolean(true));
		vAuto.add(new Boolean(false));
		/////////////////////////////////////////
		vNull.add("");
		vName.add("dateTime");
		vType.add(new Integer(java.sql.Types.TIMESTAMP));
		vPres.add(new Integer(0));
		vSearch.add(new Boolean(true));
		vAuto.add(new Boolean(false));
		/////////////////////////////////////////
		
		rs.addColumn("TYPE_NAME",vName, java.sql.Types.VARCHAR);
		rs.addColumn("DATA_TYPE",vType, java.sql.Types.INTEGER);
		rs.addColumn("PRECISION",vPres, java.sql.Types.INTEGER);
		rs.addColumn("LITERAL_PREFIX",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("LITERAL_SUFFIX",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("CREATE_PARAMS",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("CREATE_PARAMS",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("NULLABLE",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("CASE_SENSITIVE",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("SEARCHABLE",vSearch, java.sql.Types.BOOLEAN);
		rs.addColumn("UNSIGNED_ATTRIBUTE",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("FIXED_PREC_SCALE",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("AUTO_INCREMENT",vAuto, java.sql.Types.BOOLEAN);
		rs.addColumn("LOCAL_TYPE_NAME",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("LOCAL_TYPE_NAME",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("MINIMUM_SCALE",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("MAXIMUM_SCALE",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("SQL_DATA_TYPE",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("SQL_DATETIME_SUB",vNull, java.sql.Types.VARCHAR);
		rs.addColumn("NUM_PREC_RADIX",vNull, java.sql.Types.VARCHAR);
	}
	
	//////////////////////////////////////////////////////////////////////////
	private static final String COPYRIGHT="Ashpool (c)2003";
	
	private static final String S_XSD="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"" 
			+ " elementFormDefault=\"qualified\"" 
			+ " attributeFormDefault=\"unqualified\">"
			
			+ "<xs:element name=\"t\">"
					+ "<xs:annotation>"
							+ "<xs:documentation>" + COPYRIGHT + "</xs:documentation>"
					+ "</xs:annotation>"
			+"</xs:element>"
			
			+"<xs:element name=\"r\" type=\"ashpoolRow\"/>"
			+"<xs:complexType name=\"ashpoolRow\">"
					+"<xs:sequence>";
						/* column data 
							<xs:element name="id" type="xs:int" nillable="false"/>
							<xs:element name="firstname" type="xs:string" nillable="true"/>
							<xs:element name="date" type="xs:dateTime" nillable="true"/>
						 */
	private static final String E_XSD="</xs:sequence>"
			+"</xs:complexType>"
	+"</xs:schema>";
	/////////////////////////////////////////////////////////////////////////////
	private static final String S_XML="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+"<t>"
			+"<r>";
	private static final String E_XML="</r>"
		+"</t>";
		
	//////////////////////////////////////////////////////////////////////////////
	private static final String S_ELE = "<xs:element name=\"";
	private static final String S_TYP = "\" type=\"";
	private static final String S_NIL = "\" nillable=\"";
	//private static final String S_REQ = "\" use=\"";
	private static final String S_MAX = "\" maxLength=\"";
	private static final String S_AUT = "\" autonumber=\"";
	//private static final String S_DEF = "\" default=\"";
	private static final String E_ELE = "\" maxOccurs=\"unbounded\" />";
}
