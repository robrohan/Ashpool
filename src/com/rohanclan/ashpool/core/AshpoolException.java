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
 * AshpoolException.java
 *
 * Created on 2003-05-04
 */
 
package com.rohanclan.ashpool.core;

import java.io.PrintStream;
import java.io.PrintWriter;

public class AshpoolException extends Exception{
	
	private Throwable detail;
	
	public AshpoolException(String info){
		this(info, null);
	}
	
	public AshpoolException(String info, Throwable throwable){
		super(info);
		detail = throwable;
	}
	
	public AshpoolException(Throwable throwable){
		this(throwable.getMessage(), throwable);
	}
	
	public String getMessage() {
		if(detail == null) return super.getMessage();
		
		return super.getMessage();
	}
	
	public void printStackTrace(PrintStream printstream) {
		if (detail == null)
			super.printStackTrace(printstream);
		else {
			synchronized (printstream) {
				printstream.println(this);
				detail.printStackTrace(printstream);
			}
		}
	}
	
	public void printStackTrace() {
		printStackTrace(System.err);
	}
	
	public void printStackTrace(boolean verbose){
		if(verbose){
			printStackTrace(System.err);
		}
	}
	
	public void printStackTrace(PrintWriter printwriter) {
		if (detail == null){
			super.printStackTrace(printwriter);
		}else{
			synchronized (printwriter) {
				printwriter.println(this);
				detail.printStackTrace(printwriter);
			}
		}
	}
}

