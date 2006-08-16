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
 * SQLFilter.java
 *
 * Created on April 3, 2003, 10:58 PM
 */

package com.rohanclan.ashpool.core.filter;

import com.rohanclan.ashpool.core.CommandManager;
import com.rohanclan.ashpool.core.TableManager;
import com.rohanclan.ashpool.core.xml.BasicXSLEngine;

/**
 * super class for all sql filters
 * @author  rob
 */
public class SQLFilter {	
	protected TableManager tableman;
	protected String mainfile;
	protected CommandManager comman;
	
	protected BasicXSLEngine bXSL;
	
	/** Creates a new instance of SQLFilter */
	public SQLFilter(){;}
	
	public SQLFilter(TableManager tman, CommandManager com) {
		tableman = tman;
		comman = com;
		if(bXSL == null) {
			bXSL = com.getXSLEngine();
		}
	}
}
