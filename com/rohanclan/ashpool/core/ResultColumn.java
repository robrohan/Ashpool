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
 * resultColumn.java
 *
 * Created on February 2, 2003, 5:59 AM
 */

package com.rohanclan.ashpool.core;

import java.util.*;
/**
 * This is a column object. An AResultSet object is make up of one or more of
 * these.
 *
 * @author Rob Rohan
 */
public class ResultColumn {
	public String columnName;
	public int type;
	public List columnData;
	
	/** Creates a new instance of resultColumn */
	public ResultColumn() {
		columnData = new ArrayList();
	}
	
	public ResultColumn(int capacity){
		columnData = new ArrayList(capacity);
	}
	
	public int getType(){
		return type;
	}
	
	public void setType(int sqltype){
		this.type = sqltype;
	}
	
}
