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
 * ConnectionManager.java
 *
 * Created on February 1, 2003, 10:43 AM
 */

package com.rohanclan.ashpool.core;

import java.io.File;

/**
 *
 * @author  rob
 */
public class ConnectionManager 
{	
	private TableManager tableman;
	//private CommandManager comman;
	private AResultSet ars; //scratch pad result set
	
	/** Creates a new instance of ConnectionManager */
	public ConnectionManager(File datasource) throws Exception
	{
		Functions.loadFunctions();
		TableManager tm = new TableManager(datasource);
		setTableManager(tm);	
	}
	
	public void setTableManager(TableManager tm) throws Exception
	{
		tableman = tm;
		//comman = new CommandManager();
		//comman.setTableManager(tableman);
		
		ars = new AResultSet();
	}
	
	/** gets a handle to the table manager */
	public TableManager getTableManager() {
		return this.tableman;
	}
	
	/**
	 * Gets a new CommandManager and sets the table manager to the
	 * to the table manager defined in this connection.
	 * 
	 * @return
	 * @throws Exception
	 */
	public CommandManager getCommandManager() throws Exception {
		CommandManager freshcomman = new CommandManager();
		freshcomman.setTableManager(this.tableman);
		
		return freshcomman;
	}
	
	/** gets a list of tables in the datastore */
	public AResultSet getTables(String[] types) {
		ars.reset();
		getTableManager().getTables(ars, types);
		return ars;
	}
	
	public AResultSet getTables() {
		ars.reset();
		getTableManager().getTables(ars);
		return ars;
	}
	
	public AResultSet getProcedures() {
		ars.reset();
		getTableManager().getProcedures(ars);
		return ars;
	}
	
	/** 
	 * Executes a query string and returns the result set. This is the main
	 * in from the JDBC driver
	 */
	public AResultSet executeStatement(String query) throws Exception {
		//get a new commandmanager and execute the query (so local vars are local)
		CommandManager tmpexe = getCommandManager();
		
		//execute the query
		AResultSet ars = tmpexe.executeStatement(query);
		
		//null out the fresh command manager (help out GC)
		tmpexe.nullify();
				
		return ars;
	}
	
	/** executes a query string */
	public AResultSet executeStatement(String query, byte type) throws Exception {
		//return comman.executeStatement(query, type);
		return null;
	}
	
	/**
	 * For the JDBC driver to get a list of all Numeric style functions
	 * @return
	 */
	public String getNumericFunctions() {
		return Functions.getAllMathFunctions();
	}
	
	/**
	 * For the JDBC driver to get a list of all the string functions
	 * @return
	 */
	public String getStringFunctions() {
		return Functions.getAllMiscFunctions() + "," + Functions.getAllStandardFunctions(); 
	}
	
	/**
	 * For the JDBC driver to get a list of the datetime functions
	 * @return
	 */
	public String getDateTimeFunctions() {
		return "now," + Functions.getAllDateFunctions();
	}
}
