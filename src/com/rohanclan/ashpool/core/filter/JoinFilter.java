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
 * JoinFilter.java
 *
 * Created on 2003-05-04
 */

package com.rohanclan.ashpool.core.filter;

import java.util.StringTokenizer;

import com.rohanclan.ashpool.core.AshpoolException;
import com.rohanclan.ashpool.core.CommandManager;
import com.rohanclan.ashpool.core.Functions;
import com.rohanclan.ashpool.core.TableManager;

/**
 * Builds the table section of the xpath. Pretty boring class. It is added
 * for future use
 * @author  rob
 */
public class JoinFilter extends SQLFilter{

	//private SelectFilter sf;
	private String tablename="";
	private String joinfield1="";
	private String joinfield2="";
	/** inner or outer */
	private String type="inner";
	/** left or right */
	private String dir="left";
	
	public JoinFilter(TableManager tman, CommandManager com) throws Exception{
		super(tman,com);
		/* subqueries */
		//sf = new SelectFilter(tman, com);
	}
	
	public String getDirection(){
		return dir;
	}
	
	public String getType(){
		return type;
	}
	
	public String getJoinField1(){
		return joinfield1;
	}
	
	public String getJoinField2(){
		return joinfield2;
	}
	
	public String getTableName(){
		return tablename;
	}
	
	public String createXPath(String sql) throws Exception{
		//if there is a sub query here, there should be
		//some values to replace
		//not really supported yet...
		sql = Functions.unEscapeSubQueries(sql);
		sql.replaceAll("="," = ");
		
		//inner join Orders on cust_id = cust_id
		
		//should be ready to tokenize now
		StringTokenizer stok = new StringTokenizer(sql," ");
		//StringBuffer whereclause = new StringBuffer();
		try{
			//inner || outer
			type = stok.nextToken();
			
			//the word join
			stok.nextToken();
			
			//the table
			tablename = stok.nextToken();
			
			//the word on
			stok.nextToken();
			
			//field 1 the original table field
			joinfield1 = stok.nextToken();
			
			//an = sign
			stok.nextToken();
			
			//the to field the field in the second table
			joinfield2 = stok.nextToken();
			
		}catch(Exception e){
			throw new AshpoolException("Error in join clause", e);
		}
		
		return "";
	}
	
	public String toString(){
		return "t: " + tablename + " jf1: " + joinfield1 + " jf2: " + joinfield2
			+ " type " + type + " dir " + dir;
	}
	
	
}
