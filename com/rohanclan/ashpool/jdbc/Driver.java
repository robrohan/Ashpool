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
 * Driver.java
 *
 * Created on January 30, 2003, 7:09 PM
 */
 
package com.rohanclan.ashpool.jdbc;
 
import java.io.File;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.StringTokenizer;
 
/**
 *
 * @author  rob
 */
public class Driver implements java.sql.Driver 
{	
	/* try to self register */
	static
	{
		try 
		{
			java.sql.DriverManager.registerDriver(new Driver());
		}
		catch(java.sql.SQLException sqle)
		{
			System.err.println(
				"Ashpool: Unable to self register driver with java.sql.DriverManager"
			);
		}
	}
	
	/** Creates a new instance of Driver */
	public Driver() {;}
	
	public Driver newInstance()
	{
		return new Driver();
	}
	
	/** Retrieves whether the driver thinks that it can open a connection
	 * to the given URL.  Typically drivers will return <code>true</code> if they
	 * understand the subprotocol specified in the URL and <code>false</code> if
	 * they do not.
	 *
	 * @param url the URL of the database
	 * @return <code>true</code> if this driver understands the given URL;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean acceptsURL(String url) throws SQLException 
	{
		StringTokenizer stok = new StringTokenizer(url,":");
		
		if(stok.nextElement().equals("jdbc") && stok.nextElement().equals("ashpool"))
		{
			//close enough for jazz
			return true;
		}else{
			return false;
		}
	}
	
	/** Attempts to make a database connection to the given URL.
	 * The driver should return "null" if it realizes it is the wrong kind
	 * of driver to connect to the given URL.  This will be common, as when
	 * the JDBC driver manager is asked to connect to a given URL it passes
	 * the URL to each loaded driver in turn.
	 *
	 * <P>The driver should throw an <code>SQLException</code> if it is the right
	 * driver to connect to the given URL but has trouble connecting to
	 * the database.
	 *
	 * <P>The <code>java.util.Properties</code> argument can be used to pass
	 * arbitrary string tag/value pairs as connection arguments.
	 * Normally at least "user" and "password" properties should be
	 * included in the <code>Properties</code> object.
	 *
	 * @param url the URL of the database to which to connect
	 * @param info a list of arbitrary string tag/value pairs as
	 * connection arguments. Normally at least a "user" and
	 * "password" property should be included.
	 * @return a <code>Connection</code> object that represents a
	 *         connection to the URL
	 * @exception SQLException if a database access error occurs
	 *
	 * jdbc:ashpool://file:///home/rob/projects/Ashpool/datastore/;password=groovy
	 * jdbc:ashpool://file://C:\winnt\fake\xmldir\;password=groovy
	 *
	 */
	public java.sql.Connection connect(String url, java.util.Properties info) throws SQLException 
	{
		//System.out.println("Trying to connect to " + url);
		
		StringTokenizer stok = new StringTokenizer(url,":");
		
		if(stok.nextElement().equals("jdbc") && stok.nextElement().equals("ashpool"))
		{	
			//this should be the "file" word
			stok.nextElement();
				
			//this should be the path to the directory
			String storepath = stok.nextElement().toString();
			//he he he, windows uses : to denote drives - windows sucks :)
			//dont forget the token :)
			if(stok.hasMoreElements())
			{
				storepath += ":" + stok.nextElement().toString();
			}
			
			if(storepath.toString().startsWith("//"))
			{
				//try
				//{
					//System.out.println("Connecting to: " + storepath.substring(2));
					String filename = storepath.substring(2).trim();
					File datastore = new File(filename);
					
					if(!datastore.exists() || !datastore.isDirectory())
					{
						throw new SQLException(
							"Datastore either does not exsit, or is not a directory " + filename
						);
					}
					
					return new Connection(datastore, info);
				//}
				//catch(SQLException e)
				//{
				//	throw new SQLException("Could not open datastore: " + storepath.substring(2)
				//		+ ". Because: " + e.getMessage());
				//}
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	/** Retrieves the driver's major version number. Initially this should be 1.
	 *
	 * @return this driver's major version number
	 *
	 */
	public int getMajorVersion() {
		return 1;
	}
	
	/** Gets the driver's minor version number. Initially this should be 0.
	 * @return this driver's minor version number
	 *
	 */
	public int getMinorVersion() {
		return 0;
	}
	
	/** Gets information about the possible properties for this driver.
	 * <P>
	 * The <code>getPropertyInfo</code> method is intended to allow a generic
	 * GUI tool to discover what properties it should prompt
	 * a human for in order to get
	 * enough information to connect to a database.  Note that depending on
	 * the values the human has supplied so far, additional values may become
	 * necessary, so it may be necessary to iterate though several calls
	 * to the <code>getPropertyInfo</code> method.
	 *
	 * @param url the URL of the database to which to connect
	 * @param info a proposed list of tag/value pairs that will be sent on
	 *          connect open
	 * @return an array of <code>DriverPropertyInfo</code> objects describing
	 *          possible properties.  This array may be an empty array if
	 *          no properties are required.
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info) throws SQLException {
		DriverPropertyInfo driverPI[] = new DriverPropertyInfo[0];
		return driverPI;
	}
	
	/** Reports whether this driver is a genuine JDBC
	 * Compliant<sup><font size=-2>TM</font></sup> driver.
	 * A driver may only report <code>true</code> here if it passes the JDBC
	 * compliance tests; otherwise it is required to return <code>false</code>.
	 * <P>
	 * JDBC compliance requires full support for the JDBC API and full support
	 * for SQL 92 Entry Level.  It is expected that JDBC compliant drivers will
	 * be available for all the major commercial databases.
	 * <P>
	 * This method is not intended to encourage the development of non-JDBC
	 * compliant drivers, but is a recognition of the fact that some vendors
	 * are interested in using the JDBC API and framework for lightweight
	 * databases that do not support full database functionality, or for
	 * special databases such as document information retrieval where a SQL
	 * implementation may not be feasible.
	 * @return <code>true</code> if this driver is JDBC Compliant; <code>false</code>
	 *         otherwise
	 *
	 */
	public boolean jdbcCompliant() {
		return false;
	}

}
