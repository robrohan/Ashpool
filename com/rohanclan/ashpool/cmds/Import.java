/*
 * Ashpool - XML Database
 * Copyright (C) 2003 Rob Rohan
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * AshpoolCmd.java
 *
 * Created on March 20, 2003, 1:22 PM
 */

package com.rohanclan.ashpool.cmds;

import com.rohanclan.ashpool.core.TableManager;
/**
 *
 * @author  rob
 */
public class Import implements AshpoolCmd {
	
	private java.net.URL fromResource; //the file to import
	//private byte type; //the type of new resource
	private java.io.OutputStream fos; //the new table/procedure/etc
	
	public Import(String urlfrom, String to, byte type, TableManager tman) throws Exception{
		setFrom(urlfrom);
		setTo(to, type, tman);
	}
	
	public void setFrom(String urlfrom) throws Exception {
		fromResource = new java.net.URL(urlfrom);
	}
	
	public void setTo(String to, byte type, TableManager tman) throws Exception {
		fos = tman.getTableOutputStream(to,type);
	}
	
	public void doAction() throws Exception{
		
		java.io.BufferedReader ibr = new java.io.BufferedReader(
			new java.io.InputStreamReader(fromResource.openStream())
		);
		
		int i;
		
		while( (i=ibr.read()) != -1){
			fos.write(i);
		}
		
		fos.flush();
		fos.close();
		ibr.close();
		
	}
}
