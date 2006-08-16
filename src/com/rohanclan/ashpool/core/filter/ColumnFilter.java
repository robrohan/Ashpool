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
 * ColumnFilter.java
 *
 * Created on 2003-05-04
 */

package com.rohanclan.ashpool.core.filter;

import java.util.*;

import com.rohanclan.ashpool.core.AshpoolException;
import com.rohanclan.ashpool.core.CommandManager;
import com.rohanclan.ashpool.core.Functions;
import com.rohanclan.ashpool.core.TableManager;

/**
 * Builds the column section of the xpath used mostly in selects
 * @author  rob
 */
public class ColumnFilter extends SQLFilter{
	
	private SelectFilter subS;  //for sub-selects 
	private boolean doDistinct;
	private String limit;
	private List<String> fieldDis;
	private List<String> fieldVal;
	
	public ColumnFilter(TableManager tableman, CommandManager comman) throws Exception{
		super(tableman, comman);
		subS = new SelectFilter(tableman, comman);
		//subS.setXSLEngine(comman.getXSLEngine());
	}
	
	/** this passes if the distinct flag was set in the column section */
	public boolean getDistinctFlag(){
		return this.doDistinct;
	}
	
	/** if they specified a limit "top X" this will give the xpath equiv
		blank string if no limit was specified */
	public String getLimit(){
		return limit;
	}
	
	/**
	 * field display (the AS .... part)
	 */
	public List getColumnNames(){
		return fieldDis;
	}
	
	/**
	 * field value (what to select)
	 */
	public List getColumnValues(){
		return fieldVal;
	}
	
	/** loop over the sql fragment and build the xpath equiv */
	public String createXPath(String sql) throws Exception {
		sql = Functions.unEscapeSubQueries(sql);
		
		StringTokenizer stok = new StringTokenizer(sql," ");
		StringBuffer fieldclause = new StringBuffer();
		doDistinct = false;
		limit = "";
		
		fieldDis = new ArrayList<String>(); //field display (the AS .... part)
		fieldVal = new ArrayList<String>(); //field value (what to select)
		
		String fieldnames="";
		//while(!fieldnames.toLowerCase().equals("from")){
		try{
			while(stok.hasMoreElements()){
				fieldnames = stok.nextElement().toString();
				
				if(fieldnames.equals("*")){
					//select *
					fieldclause.append("<xsl:copy-of select=\"./*\"/>");
				
				}else if(fieldnames.toLowerCase().equals("distinct")){
					doDistinct = true;
				}else if(fieldnames.toLowerCase().equals("top")){
					limit = " and $pos &lt;= " + stok.nextToken();
					
				//string type functions
				}else if(Functions.isStdFunction(fieldnames.toLowerCase())){
					//is this a function?
					String func = fieldnames.toLowerCase();
					String pfun = stok.nextElement().toString();
					
					while(!pfun.equals(")")){
						func += " " + Functions.escapeOperator(pfun);
						pfun = stok.nextElement().toString();
					}
					func += pfun;
						
					//fieldclause.append(fieldnames);
					if(func.endsWith(",")){
						fieldDis.add("f" + System.currentTimeMillis());
						fieldVal.add(func.toString().trim().substring(0,func.length() -1));
					}else{
						fieldDis.add("f" + System.currentTimeMillis());
						fieldVal.add(func.toString().trim());
					}
				//replacement type functions
				}else if(Functions.isMiscFunction(fieldnames.toLowerCase())){
					String func = "";
					//the (
					stok.nextElement().toString();
					//the fieldname
					String pfun = stok.nextElement().toString();
					//the )
					stok.nextElement().toString();
						
					if(fieldnames.toLowerCase().startsWith("upper")){
						func = "upper-case(" + pfun + ")";
					}else if(fieldnames.toLowerCase().startsWith("lower")){
						func = "lower-case(" + pfun + ")";
					}else if(fieldnames.toLowerCase().startsWith("length")){
						func = "string-length(" + pfun + ")";
					}else if(fieldnames.toLowerCase().startsWith("trim")){
						func = "normalize-space(" + pfun + ")";
					}
					
					if(func.endsWith(",")){
						fieldDis.add("f" + System.currentTimeMillis());
						fieldVal.add(func.toString().trim().substring(0,func.length() -1));
					}else{
						fieldDis.add("f" + System.currentTimeMillis());
						fieldVal.add(func.toString().trim());
					} 
				//math type functions using math: exslt
				}else if(Functions.isMathFunction(fieldnames.toLowerCase())){
					//is this a function?
					String func = "math:" + fieldnames.toLowerCase();
					String pfun = stok.nextElement().toString();
					
					while(!pfun.equals(")")){
						func += " " + pfun;
						pfun = stok.nextElement().toString();
					}
					func += pfun;
						
					//fieldclause.append(fieldnames);
					if(func.endsWith(",")){
						fieldDis.add("f" + System.currentTimeMillis());
						fieldVal.add(func.toString().trim().substring(0,func.length() -1));
					}else{
						fieldDis.add("f" + System.currentTimeMillis());
						fieldVal.add(func.toString().trim());
					} 
				//date type functions using date: exslt
				}else if(Functions.isDateFunction(fieldnames.toLowerCase())){
					//is this a function?
					String func = "date:" + fieldnames.toLowerCase();
					String pfun = stok.nextElement().toString();
					
					while(!pfun.equals(")")){
						func += " " + pfun;
						pfun = stok.nextElement().toString();
					}
					func += pfun;
						
					//fieldclause.append(fieldnames);
					if(func.endsWith(",")){
						fieldDis.add("f" + System.currentTimeMillis());
						fieldVal.add(func.toString().trim().substring(0,func.length() -1));
					}else{
						fieldDis.add("f" + System.currentTimeMillis());
						fieldVal.add(func.toString().trim());
					} 
				//Aggregate Functions
				}else if(Functions.isAggFunction(fieldnames.toLowerCase())){    
					String func = fieldnames.toLowerCase();
					// (
					func += stok.nextElement().toString();
					//field name
					func += "/*/*$ASH_REPLACE_AGG/" + stok.nextElement().toString().replaceAll("\\*",".");
					// )
					func += stok.nextElement().toString();
					
					//fieldclause.append(fieldnames);
					if(func.endsWith(",")){
						fieldDis.add("f" + System.currentTimeMillis());
						fieldVal.add(func.toString().trim().substring(0,func.length() -1));
					}else{
						fieldDis.add("f" + System.currentTimeMillis());
						fieldVal.add(func.toString().trim());
					}
					//we are going to need to do a distinct filter on this resultset
					//so things look normal most of the time
					doDistinct = true;
				}else if(fieldnames.toLowerCase().equals("as")){
					//override the last display item
					fieldDis.remove(fieldDis.size()-1);
					String dspval = stok.nextElement().toString();
					if(dspval.endsWith(",")){
						fieldDis.add(dspval.trim().substring(0,dspval.length() -1));
					}else{
						fieldDis.add(dspval.trim());
					}
					
				}else if(fieldnames.equals("(")){
					try{
						String tmpq = "";
						StringBuffer subq = new StringBuffer();
						
						while(!tmpq.equals(")")){
							subq.append(" " + tmpq);
							tmpq = stok.nextElement().toString();
						}
						//System.out.println(subq.toString());
						fieldDis.add("sq" + System.currentTimeMillis());
						fieldVal.add("'" + subS.executeQuery(subq.toString(),SelectFilter.FORSINGLE) + "'");
					}catch(Exception e){
						//don't stop just show the subquery blew up 
						//TODO make better :)
						System.err.println("Subquery failed " + e.toString());
						e.printStackTrace(System.err);
					}
					
					//subquery
				//}else if(fieldnames.equals(")")){
					//end subquery
				}else if(!fieldnames.toLowerCase().equals("from")){
					//anything else should be a field name
					//start the field clause if needed
					
					//assume there is no AS keyword
					if(fieldnames.endsWith(",")){
						fieldDis.add(fieldnames.toString().trim().substring(0,fieldnames.length() -1));
						fieldVal.add(fieldnames.toString().trim().substring(0,fieldnames.length() -1));
					}else{
						fieldDis.add(fieldnames.toString().trim());
						fieldVal.add(fieldnames.toString().trim());
					}
				}
			}
		}catch(Exception e){
			throw new AshpoolException(
				"Problem in the Column clause near " + fieldnames,
				e
			);
		}
		
		return fieldclause.toString();
	}
}
