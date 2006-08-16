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
 * Functions.java
 *
 * Created on April 11, 2003, 6:54 PM
 */

package com.rohanclan.ashpool.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author  rob
 */
public class Functions 
{	
	public static List<String> stdfunctions    = new ArrayList<String>();
	public static List<String> datefunctions   = new ArrayList<String>();
	public static List<String> mathfunctions   = new ArrayList<String>();
	public static List<String> aggfunctions    = new ArrayList<String>();
	public static List<String> miscfunctions   = new ArrayList<String>();
		
	private Functions(){;}

	public static void loadFunctions()
	{
		//TODO make a function object that had more info
		
		//standard functions (need no namespace)
		stdfunctions.add("concat"); 
		stdfunctions.add("contains"); 
		stdfunctions.add("starts-with");
		stdfunctions.add("ends-with");
		stdfunctions.add("string-length");
		stdfunctions.add("substring");
		stdfunctions.add("substring-after");
		stdfunctions.add("substring-before");
		stdfunctions.add("round");
		stdfunctions.add("number");
		stdfunctions.add("name");
		stdfunctions.add("upper-case");
		stdfunctions.add("lower-case");
		
		//date namespace functions
		datefunctions.add("date-time");
		datefunctions.add("date");
		datefunctions.add("time");
		datefunctions.add("year");
		datefunctions.add("leap-year");
		datefunctions.add("month-in-year");
		datefunctions.add("month-name");
		datefunctions.add("month-abbreviation");
		datefunctions.add("week-in-year");
		datefunctions.add("week-in-month");
		datefunctions.add("day-in-year");
		datefunctions.add("day-in-month");
		datefunctions.add("day-of-week-in-month");
		datefunctions.add("day-in-week");
		datefunctions.add("day-abbreviation");
		datefunctions.add("hour-in-day");
		datefunctions.add("minute-in-hour");
		datefunctions.add("second-in-minute");
		
		//math namespace functions
		mathfunctions.add("abs");
		mathfunctions.add("acos");
		mathfunctions.add("asin");
		mathfunctions.add("atan");
		mathfunctions.add("atan2");
		mathfunctions.add("constant");
		mathfunctions.add("cos");
		mathfunctions.add("exp");
		mathfunctions.add("highest");
		mathfunctions.add("log");
		mathfunctions.add("lowest");
		mathfunctions.add("power");
		mathfunctions.add("random");
		mathfunctions.add("sin");
		mathfunctions.add("sqrt");
		mathfunctions.add("tan");
		mathfunctions.add("ceiling");
		
		//agg functions
		aggfunctions.add("sum");
		aggfunctions.add("count");
		aggfunctions.add("max");
		aggfunctions.add("min");
		aggfunctions.add("avg");
		
		//misc function (mostly these are replacement functions)
		miscfunctions.add("upper");
		miscfunctions.add("lower");
		miscfunctions.add("length");
		miscfunctions.add("trim");
		
	}
	
	public static boolean isDateFunction(String funcName){
		return datefunctions.contains(funcName);
	}
	public static boolean isMathFunction(String funcName){
		return mathfunctions.contains(funcName);
	}
	public static boolean isAggFunction(String funcName){
		return aggfunctions.contains(funcName);
	}
	public static boolean isStdFunction(String funcName){
		return stdfunctions.contains(funcName);
	}
	public static boolean isMiscFunction(String funcName){
		return miscfunctions.contains(funcName);
	}
	
	//////////////////////////////////////////////////////
	// get all the functions as comma lists. This is mostly for
	// the JDBC driver
	public static String getAllMathFunctions(){
		return arrayListToCommaList(mathfunctions);
	}
	
	public static String getAllDateFunctions(){
		return arrayListToCommaList(datefunctions);
	}

	public static String getAllStandardFunctions(){
		return arrayListToCommaList(stdfunctions);
	}
	
	public static String getAllMiscFunctions(){
		return arrayListToCommaList(miscfunctions);
	}
	
	public static String getAshpoolKeywords(){
		return "top,env,unset,$identity,exec,tables,procedures,columns,import";
	}
	
	/** turns an List into a comma delim string */
	public static String arrayListToCommaList(List inlist){
		StringBuffer funlist = new StringBuffer();
		for(Iterator i = inlist.iterator(); i.hasNext();){
			funlist.append(i.next().toString() + ",");
		}
		return funlist.toString().substring(0,funlist.length() -1);
	}
	
	/** replaces quoted text with an #[number]# and stores the number with
		the string in the passed Map object */
	private static Pattern p = Pattern.compile("(['][a-zA-Z0-9\\&;,\"!@#$%_`~\\-\\?\\.\\*\\+ \\t\\r\\n\\(\\)]*['])");
	
	public static String placeHoldStrings(String sql, Map<String,String> keeper){
		StringBuffer sb = new StringBuffer();
		Matcher m = p.matcher(sql);
		
		int repid = 0;
		while (m.find()) {
			//System.out.println("S:" + m.start() + " E:" + m.end() + " L:" + sql.length());
			keeper.put(""+repid, sql.substring(m.start(),m.end()).toString());
			m.appendReplacement(sb, "#" + repid + "#");
			repid++;
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	/** after the query has been processed the quoted strings should go back
		into the query fragments - this undoes the placeHolderString
		Function */
	private static Pattern unp = Pattern.compile("#[0-9]+#");
	
	public static String unplaceHoldStrings(String sqlfrag, Map lookup){
		if(sqlfrag.indexOf('#') < 0){
			return sqlfrag;
		}else{
			StringBuffer sb = new StringBuffer();
			Matcher m = unp.matcher(sqlfrag);
			
			String key = "";
			while (m.find()) {
				//System.out.println("S:" + m.start() + " E:" + m.end() + " L:" + sql.length());
				key = sqlfrag.substring(m.start()+1,m.end()-1);
				m.appendReplacement(sb, (String)lookup.get(key));
			}
			m.appendTail(sb);
			
			return sb.toString();
		}
	}
	
	/** some keywords are used to "split" the sql string (select
		filter most notably). this changes any spliting word to lower
		case - the regexp's are bad though - needs something a bit
		more specific */
	public static String unCaseKeywords(String sql){
		sql = sql.replaceAll("[Ss][Ee][Ll][Ee][Cc][Tt]","select");
		sql = sql.replaceAll("[Ff][Rr][Oo][Mm]","from");
		sql = sql.replaceAll("[Ww][Hh][Ee][Rr][Ee]","where");
		sql = sql.replaceAll("[Oo][Rr][Dd][Ee][Rr] [Bb][Yy]","order by");
		sql = sql.replaceAll("[Ii][Nn][Nn][Ee][Rr] [Jj][Oo][Ii][Nn]","inner join");
		sql = sql.replaceAll("[Oo][Uu][Tt][Ee][Rr] [Jj][Oo][Ii][Nn]","outer join");
		sql = sql.replaceAll("[Uu][Pp][Dd][Aa][Tt][Ee]","update");
		sql = sql.replaceAll("[Ss][Ee][Tt]","set");
		sql = sql.replaceAll("[Dd][Ee][Ll][Ee][Tt][Ee]","delete");
		return sql;
	}
	
	public static String escapeSubQueries(String sql){
		sql = sql.replaceAll("(\\([ ]*)[Ss][Ee][Ll][Ee][Cc][Tt]","$1 %ASHPOOLSELECT%");
		sql = sql.replaceAll("(\\([ a-zA-Z0-9_\\$\\%]*)[Ff][Rr][Oo][Mm]([ \\\"\\'a-zA-Z0-9_\\%\\$\\=]*\\))","$1 %ASHPOOLFROM% $2");
		sql = sql.replaceAll("[Ww][Hh][Ee][Rr][Ee]([a-zA-Z0-9_\\%\\=\\\"\\' ]*\\))","%ASHPOOLWHERE% $1");
		sql = sql.replaceAll("([IiOo][NnUu][NnTt][Ee][Rr] [Jj][Oo][Ii][Nn])", "%ASHPOOLJOIN% $1");
		return sql;
	}
	
	public static String unEscapeSubQueries(String sql){
		sql = sql.replaceAll("%ASHPOOLSELECT%","select");
		sql = sql.replaceAll("%ASHPOOLFROM%","from");
		sql = sql.replaceAll("%ASHPOOLWHERE%","where");
		return sql;
	}
	
	/** convert normal sql operators to xml operators */
	public static String escapeOperator(String inop){
		String opr = "";
		if(inop.equals(">")){
			opr = "&gt;";
		}else if(inop.equals("<")){
			opr = "&lt;";
		}else if(inop.equals(">=")){
			opr = "&gt;=";
		}else if(inop.equals("<=")){
			opr = "&lt;=";
		}else if(inop.equals("<>")){
			opr = "!=";
		}else if(inop.toLowerCase().trim().equals("like")){
			opr = "= contains(";
		}else if(inop.equals("/")){
			opr = " div ";
		}else if(inop.equals("%")){
			opr = " mod ";
		}else{
			opr = inop;
		}
		return opr;
	}
}