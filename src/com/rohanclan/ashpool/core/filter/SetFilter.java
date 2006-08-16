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
 * WhereFilter.java
 *
 * Created on 2003-05-04
 */

package com.rohanclan.ashpool.core.filter;

import java.util.List;
import java.util.StringTokenizer;

import com.rohanclan.ashpool.core.AshpoolException;
import com.rohanclan.ashpool.core.CommandManager;
import com.rohanclan.ashpool.core.TableManager;

/**
 * Builds the where section of the xpath
 * @author  rob
 */
public class SetFilter extends SQLFilter{
	
	//private SelectFilter subS;  //for sub-selects 
	
	public SetFilter(TableManager tableman, CommandManager comman) throws Exception{
		super(tableman, comman);
		//subS = new SelectFilter(tableman, comman);
	}
	
	/** parses the sql fragment and tries to populate the passed
		Lists with the columnnames and values */
	public void getColumnsAndValues(String sql, List<String> insertColumnNames, List<String> insertColumnValues) 
		throws AshpoolException{
		//the next bit expects the sql fragment to have a 'where' at the end
		//so we'll tack one on - this should probably change eventually.
		sql += " where ";
		
		StringTokenizer stok = new StringTokenizer(sql," ");
		String currenttok = "";
		
		try{
			while(!currenttok.toLowerCase().equals("where") && stok.hasMoreElements()){
				currenttok = stok.nextToken();
				
				if(currenttok.toLowerCase().equals("where")) break;
				//if we get a stray comma
				if(currenttok.toLowerCase().equals(",")) currenttok = stok.nextToken();
				
				//add the column
				insertColumnNames.add(currenttok);
				//should be =
				currenttok = stok.nextToken();
				
				//should be the value to set to
				currenttok = stok.nextToken();
				
				if(currenttok.trim().startsWith("'") && (currenttok.trim().endsWith("'") || currenttok.trim().endsWith("',")) ){
					if(currenttok.endsWith(",")){
						insertColumnValues.add(currenttok.substring(0,currenttok.length()-1));
					}else{
						insertColumnValues.add(currenttok);
					}
				//if it is multi word text
				}else if(currenttok.trim().startsWith("'") || currenttok.trim().equals("'")){
					StringBuffer buff = new StringBuffer(currenttok);
					currenttok = stok.nextToken();
					
					while(! (currenttok.trim().endsWith("'") || currenttok.trim().endsWith("',")) ){
						buff.append(" " + currenttok);
						currenttok = stok.nextToken();
					}
					buff.append(" " + currenttok);
					
					if(buff.toString().trim().endsWith(",")){
						insertColumnValues.add(buff.toString().substring(0,buff.toString().length()-1));
					}else{
						insertColumnValues.add(buff.toString());
					}
				}else if(currenttok.trim().startsWith("(") || currenttok.trim().equals("(") ){
					//sub query
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
					
				//simple value
				}else{
					if(currenttok.trim().endsWith(",")){
						insertColumnValues.add(currenttok.substring(0,currenttok.length()-1));
					}else{
						insertColumnValues.add(currenttok);
					}
				}
			}
		}catch(Exception e){
			throw new AshpoolException("Problem in set clause near " + currenttok, e);
		}
		
	}
	
	public String createXPath(String sql) throws Exception {
		return "";
	}
	
}
