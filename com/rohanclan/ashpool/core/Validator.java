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
 * Validator.java
 *
 * Created on March 15, 2003, 4:55 PM
 */

package com.rohanclan.ashpool.core;

import java.text.ParseException;

/**
 * Uses schema information to validate field infomation (mostly used on inserts)
 * @author  rob
 */
public class Validator {
	
	/** Creates a new instance of Validator */
	public Validator() {;} 
	
	/** returns the modified (if needed) field value or if the field is 
	 * totally wrong throws a error 
	 */
	public static String validate(String field, String datatype, String len) throws AshpoolException {
		if(len == null || len.length() == 0) len = "0";
		return validate(field, Short.parseShort(datatype), Integer.parseInt(len));
	}
	
	/** returns the modified (if needed) field value or if the field is 
	 * totally wrong throws a error 
	 */
	public static String validate(String field, String datatype) throws AshpoolException {
		return validate(field, Short.parseShort(datatype), 0);
	}
	
	/** returns the modified (if needed) field value or if the field is 
	 * totally wrong throws a error 
	 */
	public static String validate(String field, String datatype, int len) throws AshpoolException {
		return validate(field, Short.parseShort(datatype), len);
	}
	
	/** returns the modified (if needed) field value or if the field is 
	 * totally wrong throws a error 
	 */
	public static String validate(String field, short datatype, int len) throws AshpoolException {
		java.text.SimpleDateFormat isoformatter;
		java.text.SimpleDateFormat stdformatter;
		
		if(field.toString().toLowerCase().equals("null") 
			|| field.toString().equals("''")
			|| field.toString().trim().equals("")
			|| field.toString().trim().equals("&#39;")
			){
			return "";
		}
		
		switch(datatype){
			case java.sql.Types.CHAR:
			case java.sql.Types.VARCHAR:
				//should be in quotes for a varchar
				if(!field.trim().startsWith("'") || !field.trim().endsWith("'")){
					//throw new SQLException("Syntax error with varchar(string) value: " + field);
				
				//this is a touch sketchy. when an insert of '' (blank string) hits
				//command manager it replaces it with &#39; to escape things like
				//Rob's - so if we get that then they tried to enter a blank field
				//and command manger thought it was a '
				/*}else"*/ 
				}else{
					//remove the quotes
					field = field.trim().substring(1,field.length() -1);
				}
				
				//if we are passed a length make sure it's not too long
				if(len > 0){
					if(len < field.length()) 
						throw new AshpoolException("string truncation (string too long) " + field);
						//throw new SQLException("string truncation (string too long) " + field);
				}
				//guess thats it, return string for insertion
				return field;
				
			case java.sql.Types.BOOLEAN:
				//be nice and try to guess what they want
				if(field.toLowerCase().endsWith("true") || field.equals("1") 
					|| field.toLowerCase().equals("yes")){
					return "true";
				}else if(field.toLowerCase().endsWith("false") || field.equals("0") || field.toLowerCase().equals("no")){
					return "false";
				}else{
					throw new AshpoolException("Can not make " + field + " into a boolean");
					//throw new SQLException("Can not make " + field + " into a boolean");
				}
							   
			case java.sql.Types.DATE:
				isoformatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
				stdformatter = new java.text.SimpleDateFormat("MM/dd/yyyy");
				
				//if they used the now function send back the proper date
				if(field.toLowerCase().equals("now") || field.toLowerCase().equals("date")){
					return isoformatter.format(new java.util.Date());
				}
				
				//try to parse the string for a date first try iso
				try{
					return isoformatter.format(isoformatter.parse(field.trim()));
				}catch(ParseException e){
					//not iso, try local US format
					try{
						return isoformatter.format(stdformatter.parse(field.trim()));
					}catch(ParseException e1){
						//throw new SQLException(
						throw new AshpoolException(
							"Can not parse date. Try using iso format yyyy-mm-dd or " 
							+ stdformatter.format(new java.util.Date()) 
							+ " Error with: " + field
						);
					}
				}
			
			case java.sql.Types.TIMESTAMP:
				isoformatter = new java.text.SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
				//locale specific
				stdformatter = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
				//dbtime stamp {ts '2003-03-31 00:00:00'}
				java.text.SimpleDateFormat tsformatter =
					new java.text.SimpleDateFormat("'{ts 'yyyy-MM-dd hh:mm:ss'}'");
				
				if(field.startsWith("'") && field.endsWith("'")){
					field = field.substring(1,field.length()-1);
				}
				
				//if they used the now function send back the proper datetime
				if(field.toLowerCase().equals("now") || field.toLowerCase().equals("date-time")){
					return isoformatter.format(new java.util.Date());
				}
				
				//try to parse the string for a datetime first try iso
				try{
					return isoformatter.format(isoformatter.parse(field.trim()));
				}catch(ParseException e){
					//not iso try local US format
					try{
						return isoformatter.format(stdformatter.parse(field.trim()));
					}catch(ParseException e1){
						//not local US try timestamp
						try{
							return isoformatter.format(tsformatter.parse(field.trim()));    
						}catch(ParseException e2){
							//throw new SQLException(
							throw new AshpoolException(
								"Can not parse datetime. Try using iso format yyyy-MM-ddThh:mm:ss " + field
							);
						}
					}
				}
				
			case java.sql.Types.INTEGER:
				try{
					Integer.parseInt(field);
				}catch(Exception e){
					//throw new SQLException(
					throw new AshpoolException(
						"'" + field + "' does not seem to be an integer",
						e
					);
					
				}
				return field;
			
			case java.sql.Types.FLOAT:
			case java.sql.Types.DECIMAL:
				try{
					Float.parseFloat(field);
				}catch(Exception e){
					//throw new SQLException(
					throw new AshpoolException(
						field + " does not seem to be a float or decimal",
						e
					);
				}
				return field;
				
			case java.sql.Types.DOUBLE:
				try{
					Double.parseDouble(field);
				}catch(Exception e){
					//throw new SQLException(
					throw new AshpoolException(
						field + " does not seem to be a double",
						e
					);
				}
				return field;
				 
			//assume all is well
			default:
				return field;
		}
	}
}
