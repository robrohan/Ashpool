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
 * TableFilter.java
 *
 * Created on 2003-05-04
 */

package com.rohanclan.ashpool.core;


/**
 * Builds the table section of the xpath. Pretty boring class. It is added
 * for future use
 * @author  rob
 */
public class TableFilter extends SQLFilter{

	private String tablename;
	
	public TableFilter(TableManager tman, CommandManager com) throws Exception{
		super(tman,com);
		//sf = new SelectFilter(tman, com);
	}
	
	public String getTableName(){
		return tablename;
	}
	
	public String createXPath(String sql) throws Exception{
		tablename = sql.trim();
		return tablename;
	}

}
