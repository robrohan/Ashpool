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
 * OrderFilter.java
 *
 * Created on 2003-05-04
 */

package com.rohanclan.ashpool.core;

import java.util.*;

/**
 * Builds the order by section of the xpath
 * @author  rob
 */
public class OrderFilter extends SQLFilter{

	private SelectFilter sf;
	private String mainfile;
	
	public OrderFilter(TableManager tman, CommandManager com) throws Exception{
		super(tman,com);
		sf = new SelectFilter(tman, com);
	}
	
	public void setTable(String to){
		mainfile = to;
	}
	
	public String createXPath(String sql) throws Exception {
		StringTokenizer stok = new StringTokenizer(sql," ");
		StringBuffer orderbyclause  = new StringBuffer();
		//StringBuffer orderbyclauseU = new StringBuffer();
		
		java.util.Map cols = new java.util.HashMap();
		//we will need to get the datatypes to sort correctly
		//if(stok.hasMoreElements()){
		try{
			AResultSet ars = new AResultSet();
			sf.getTableColumns(mainfile, ars);
			
			while(ars.next()){
				cols.put(
					ars.getString(1), 
					ars.getString(5)
				);
			}
			ars = null;
		}catch(Exception e){
			System.err.println("Could not get column data types " + e.toString());
			//e.printStackTrace(System.err);
		}
		//}
		
		//only need to override if desc
		String xslorder = "ascending";
		//text or numeric
		String xsltype = "text";
		
		StringBuffer currentkeyword = new StringBuffer();
		while(stok.hasMoreElements()){
			//String currentkeyword = stok.nextElement().toString().toLowerCase();
			currentkeyword.delete(0, currentkeyword.length());
			currentkeyword.append(stok.nextElement().toString());
			
			if(currentkeyword.toString().toLowerCase().equals("by")){
				//first run
			}else if(currentkeyword.toString().toLowerCase().equals("desc") 
				|| currentkeyword.toString().toLowerCase().equals("desc,")){
					
				xslorder = "descending";
				orderbyclause.append(O_sort + xslorder + D_sort + xsltype + E_sort);
			}else if(currentkeyword.toString().toLowerCase().equals("asc") 
				|| currentkeyword.toString().toLowerCase().equals("asc,")){
					
				xslorder = "ascending";
				orderbyclause.append(O_sort + xslorder + D_sort + xsltype + E_sort);
			}else{
				//this is a column specificaion this could end with a comma
				if(currentkeyword.toString().endsWith(",") || !stok.hasMoreElements()){
					//remove the comma
					if(currentkeyword.toString().endsWith(","))
						currentkeyword.delete(currentkeyword.length() - 1,currentkeyword.length());
					
					String dtype = cols.get(currentkeyword.toString()).toString();
					if(dtype != null){
						if(dtype.equals("xs:integer")
						   || dtype.equals("xs:decimal")
						   || dtype.equals("xs:float")
						   || dtype.equals("xs:double") ){
							xsltype = "number"; 
						}else{
							xsltype = "text";
						}
					}
					
					//default asc
					orderbyclause.append(S_sort + currentkeyword 
						+ O_sort + "ascending" 
						+ D_sort + xsltype + E_sort
					);
				//otherwise prepare to find the proper order
				}else{
					String dtype = cols.get(currentkeyword.toString()).toString();
					if(dtype != null){
						if(dtype.equals("xs:integer")
						   || dtype.equals("xs:decimal")
						   || dtype.equals("xs:float")
						   || dtype.equals("xs:double") ){
							xsltype = "number"; 
						}else{
							xsltype = "text";
						}
					}
					orderbyclause.append(S_sort + currentkeyword);
				}
			}
		}
		return orderbyclause.toString();
	}
	
	private static final String S_sort ="<xsl:sort select=\"";
	private static final String O_sort ="\" order=\"";
	private static final String D_sort ="\" data-type=\"";
	private static final String E_sort ="\"/>";
	
}
