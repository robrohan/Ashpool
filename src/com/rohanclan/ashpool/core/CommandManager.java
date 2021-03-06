/*
 * Ashpool - XML Database
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
 * CommandManager.java
 *
 * Created on February 1, 2003, 10:15 AM
 */

package com.rohanclan.ashpool.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.rohanclan.ashpool.cmds.Import;
import com.rohanclan.ashpool.cmds.RunBSF;
import com.rohanclan.ashpool.core.filter.CreateFilter;
import com.rohanclan.ashpool.core.filter.DeleteFilter;
import com.rohanclan.ashpool.core.filter.InsertFilter;
import com.rohanclan.ashpool.core.filter.SelectFilter;
import com.rohanclan.ashpool.core.filter.UpdateFilter;
import com.rohanclan.ashpool.core.xml.BasicXSLEngine;

/**
 * Tries to figure out what the requested action really wants, and most often
 * returns a AResultSet object. 
 * @author  rob
 */ 
public class CommandManager 
{
	/** the select filter */
	public SelectFilter select_filter;
	/** the create filter */
	public CreateFilter create_filter;
	/** the insert filter */
	public InsertFilter insert_filter;
	/** the delete filter */
	public DeleteFilter delete_filter;
	/** the update filter */
	public UpdateFilter update_filter;
	
	private AResultSet queryresults;
	private BasicXSLEngine bXSL;
	
	/** handle to the tablemanager (for physical file changes) */
	private TableManager tableman;
	
	/** local and global variables */
	private Map<String,Object> variables;
	private static Map<String,Object> globalvariables;
	
	private RunBSF bsf = null;
	
	/** Creates a new instance of CommandManager */
	public CommandManager() {
		//what we are going to pass back to the driver
		queryresults = new AResultSet();
		
		//the engine used to query the table... not really query by execute...
		bXSL = new BasicXSLEngine();
		
		//new local variable scope
		variables = new HashMap<String, Object>();
		
		//if no global scope, make one
		if(globalvariables == null) {
			globalvariables = new HashMap<String,Object>();
			//system specific variables
			globalvariables.put("SYS:DEBUG",new Boolean(false));
			globalvariables.put("SYS:STATS",new Boolean(true));
		}
		//variables.put("DEBUG",new Boolean(false));
		
		//if bsf is loaded or we don't know if it is try to load it
		if(getGlobalVariable("SYS:BSF") == null || ((Boolean)getGlobalVariable("SYS:BSF")).booleanValue() == true) { 
			try {
				bsf = new RunBSF(this);
				globalvariables.put("SYS:BSF",new Boolean(true));
			} catch(Exception e) {
				globalvariables.put("SYS:BSF",new Boolean(false));
			}
		}
	}
	
	/** get access to the table manager */
	public void setTableManager(TableManager tman) throws Exception {
		tableman = tman;
		
		//we will, more than likely, need a some filters.
		select_filter = new SelectFilter(tableman, this);
		create_filter = new CreateFilter(tableman, this);
		delete_filter = new DeleteFilter(tableman, this);
		insert_filter = new InsertFilter(tableman, this);
		update_filter = new UpdateFilter(tableman, this);
	}
	
	public BasicXSLEngine getXSLEngine()
	{
		return bXSL;
	}
	
	/** gets the select filter */
	public SelectFilter getSelectFilter()
	{
		return select_filter;
	}
	
	/** gets the create filter */
	public CreateFilter getCreateFilter()
	{
		return create_filter;
	}
	
	/** gets the Insert filter */
	public InsertFilter getInsertFilter()
	{
		return insert_filter;
	}
	
	public UpdateFilter getUpdateFilter()
	{
		return update_filter;
	}
	
	public DeleteFilter getDeleteFilter()
	{
		return delete_filter;
	}
	
	/** gets the table manager */
	public TableManager getTableManager(){
		return tableman;
	}
	
	public Map getVariables() {
		return variables;
	}
	
	public void setVariable(String name, Object value) {
		variables.put(name,value);
	}
	
	public Object getVariable(String name) {
		return variables.get(name);
	}
	
	public void removeVariable(String name) {
		if(variables.containsKey(name)){ // && !name.equals("DEBUG")
			variables.remove(name);
		}
	}
	
	public Map getGlobalVariables() {
		return globalvariables;
	}
	
	public void setGlobalVariable(String name, Object value){
		globalvariables.put(name,value);
	}
	
	public Object getGlobalVariable(String name){
		return globalvariables.get(name);
	}
	
	public void removeGlobalVariable(String name){
		if(globalvariables.containsKey(name)  && !name.equals("SYS:DEBUG")){
			globalvariables.remove(name);
		}
	}
	
	public String replaceVariables(String withrefs){
		//replace global vars
		int lvars = globalvariables.keySet().size();
		Object varnames[] = globalvariables.keySet().toArray();
		//replace local vars
		for(int x=0;x<lvars;x++){
			withrefs = withrefs.replaceAll(
				"\\$" + varnames[x].toString(),
				globalvariables.get(varnames[x]).toString()
			);
		}
		lvars = variables.keySet().size();
		varnames = variables.keySet().toArray();
		//replace local vars
		for(int x=0;x<lvars;x++){
			withrefs = withrefs.replaceAll(
				"\\$" + varnames[x].toString(),
				variables.get(varnames[x]).toString()
			);
		}
		return withrefs;
	}
	
	/** trys to set the local variable from a name=value pair */
	public void setVariableString(String namevalue) throws Exception {
		StringTokenizer stok = new StringTokenizer(namevalue, "=");
		String varname = stok.nextToken();
		//System.out.println(varname);
		String value="";
		
		while(stok.hasMoreTokens()){
			value += stok.nextToken() + "=";
		}
		//System.out.println(value.trim().substring(0,value.trim().length() - 1));
		value = value.trim().substring(0,value.trim().length() - 1).trim();
		
		//now, figure out if its a sub-query or just a normal set
		if(value.startsWith("(")){
			variables.put(
				varname,
				select_filter.executeQuery(
					replaceVariables(value.substring(1,value.length()-1)),
					SelectFilter.FORSINGLE
				)
			);
			
		}else{
			variables.put(varname.trim(),value.trim());
		}
	}
	
	public AResultSet executeXPathStatement(String query) throws Exception{
		return null;
	}
	
	/** execute an sql style query. Try to figure out the needed filter, and
	 * send the command to that filter. This may be a bit short sighted
	 * depending on how crazy I want to let the sql statments get
	 */
	private boolean fatal = false;
	
	public AResultSet executeSQLStatement(String query) throws Exception {
		int i=0;
		try {
			String[] statements = query.split(";");
			
			//and do each statement by it's self
			for(i=0; i<statements.length; i++){
			
				//if this or any other sub-process (a called procedure for
				//example) blew up we should stop everything.
				//Since this function is called recursivly, we need to know
				//when an error some where else happened.
				if(fatal) 
					break;
			
				query = statements[i].trim();
				
				//ignore commments
				if(query.startsWith("--")) continue;
				
				//run any BSF commands before we do any string replacements
				if(query.startsWith("%")) {
					if(bsf != null) {
						bsf.setScript(query.trim().substring(1));
						bsf.doAction();
					} else {
						throw new SQLException(
							"scripting libraries do not seem to exist key % is disabled"
						);
					}
					continue;
				}
				
				//clean out pretty white space (should be regex?)
				//space needs to be there for scripts thats why it is moved 
				//here
				query = query.replace('\n', ' ');
				query = query.replace('\r', ' ');
				query = query.replace('\t', ' ');
				
				//A couple things can take place before the variables get replaced
				//in the commands. Namely select $variable
				if(query.indexOf("$") > 0 && query.length() <= 28){
					StringTokenizer tmp = new StringTokenizer(query, " ");
					if(query.toLowerCase().trim().startsWith("select")
						&& tmp.countTokens() == 2){
						//select
						tmp.nextToken();
						//$name
						String varname = tmp.nextToken().substring(1);
						
						queryresults.reset();
						//try local scope first if not there...
						if(variables.containsKey(varname)){
							queryresults.setQuickResultSet(
								varname,
								variables.get(varname).toString()
							);
						//try global scope
						}else if(globalvariables.containsKey(varname)){
							queryresults.setQuickResultSet(
								varname,
								globalvariables.get(varname).toString()
							);
						}
						continue;
					}
				}
				
				//loop over all the stored vars and replace any values we
				//find with the proper items - NOTE this could get hairy
				//if there are a lot of variables.
				if(query.indexOf("$") > 0){
					query = replaceVariables(query);
				}
				
				//these will break the engine so we have to replace them here
				query = query.replaceAll("&", "&amp;");
				query = query.replaceAll("''", "&#39;");
				
				//this keeps fragments from being inserted :-/
				query = query.replaceAll("<", "&lt;");
				query = query.replaceAll(">", "&gt;");
				
				if(query.length() <= 0) 
					continue;
				
				//we should be able to tokenize now
				StringTokenizer stok = new StringTokenizer(query," ");
				
				//parse the sting and figure out what needs to be done
				String firstkeyword = stok.nextToken().toLowerCase();
				
				//need 2 words for some items and case is important
				//but some commands do not have 2 words (i.e. "done")
				String secondkeyword = "";
				if(stok.hasMoreTokens()){
					secondkeyword = stok.nextToken();
				}
				
				//execute the command or list of commands
				if(firstkeyword.equals("drop")) {
					if(secondkeyword.toLowerCase().equals("table")) {
						tableman.doDropTable(stok.nextToken());
					} else if(secondkeyword.toLowerCase().equals("procedure")) {
						tableman.doDropProcedure(stok.nextToken());
					} else {
						throw new SQLException("I don't understand the command: " + query);
					}
				} else if(firstkeyword.equals("done")) {
					break;
				
				//execute a stored procedure
				} else if(firstkeyword.equals("exec")) {
					String proccommand = query.substring(4).trim();
					
					StringBuffer storedproc = new StringBuffer();
					String proc = "";
					//sp_dosomthing var1=test, var2=1234, var3='hello'
					String[] procparts = proccommand.split(",");
					
					//the first word should be the proc name
					int fspace = procparts[0].indexOf(" ");
					if(fspace > -1){
						proc = procparts[0].substring(0,fspace);
						procparts[0] = procparts[0].substring(fspace);
					}else{
						proc = procparts[0].trim();
						procparts = null;
					}
					
					//read the procs contents
					BufferedInputStream isr = new BufferedInputStream(
						tableman.getProcedureInputStream(proc)
					);
					
					int ch;
					while( (ch = isr.read()) != -1 ){
						storedproc.append((char)ch);
					}
					
					//if there are any variables to set, try to set them
					if(procparts != null){
						int plen = procparts.length;
						for(int z = 0; z<plen; z++){
							//set any variables to the local scope
							setVariableString(procparts[z].trim());
						}
					}
					//now try to execute the commands in the stored proc
					queryresults = executeSQLStatement(storedproc.toString());
					
				//if it looks like a select statement
				}else if(firstkeyword.equals("select")) {
					String tname;
					queryresults.reset();
					
					//command to get a list of available tables?
					if(query.toLowerCase().trim().equals("select tables")) {
						tableman.getTables(queryresults);
						
					} else if(query.toLowerCase().trim().equals("select procedures")) {
						tableman.getProcedures(queryresults);
						
					//currently supported database types?
					} else if(query.toLowerCase().trim().equals("select types")) {
						create_filter.getSupportedDataTypes(queryresults);
					
					//command to get a simple test ResultSet
					} else if(query.toLowerCase().trim().equals("select test")) {
						queryresults.setQuickResultSet("Test_Query", "This is the result");
					
					} else if(query.toLowerCase().trim().equals("select env")) {
						List<Object> names = new ArrayList<Object>(variables.keySet());
						List<Object> values = new ArrayList<Object>(variables.values());
						queryresults.addColumn("name", names, Types.VARCHAR);
						queryresults.addColumn("value", values, Types.VARCHAR);
						
						continue;
					
					//selecting the columns from a table. WARNING this seems a bit
					//risky as the command is "select columns <tablename>" very close
					//to a normal query!
					} else if(secondkeyword.toLowerCase().equals("columns")
						&& !(tname = stok.nextElement().toString()).toLowerCase().equals("from")) {
					
						//try to send it to a select filter
						select_filter.getTableColumns(tname, queryresults);
					} else {
						//fill the pass recordset with the passed sql query
						select_filter.executeQuery(query, queryresults);
					}
					
				} else if(firstkeyword.equals("set")) {
					setVariableString(query.substring(3).trim());
					
				} else if(firstkeyword.equals("unset")) {
					removeVariable(secondkeyword.trim()); 
					
				} else if(firstkeyword.equals("update")) {
					//doesnt return anything
					update_filter.executeQuery(query, queryresults);
					
				} else if(firstkeyword.equals("delete")) {
					//doesnt return anything
					delete_filter.executeQuery(query, queryresults);
					
				} else if(firstkeyword.equals("insert")) {
					//doesnt return anything
					insert_filter.executeQuery(query, queryresults);
					
				} else if(firstkeyword.equals("create")) {
					//doesnt return anything
					if(secondkeyword.toLowerCase().equals("table")) {
						create_filter.executeQuery(query, queryresults);
					}
					
					if(secondkeyword.toLowerCase().equals("database")) {
						//create database [dbname] [[root_user] [password] [encryption_type]]
						String dbname = stok.nextToken();
						//String ruser  = 
						stok.nextToken();
						//String password = 
						stok.nextToken();
						//String enctype = 
						stok.nextToken();
						
						try {
							File newdb = new File(dbname);
							//if the dir is not there try to make it
							if(!newdb.exists()) { 
								newdb.mkdir(); 
							}
						} catch(Exception e) {
							//e.printStackTrace(System.err);
							throw new SQLException("Create Error: " + e.toString());
						}
						
						continue;
					}
					
				} else if(firstkeyword.equals("alter")) {
					if(secondkeyword.toLowerCase().equals("sequence")) {
						tableman.setSequenceStart(stok.nextToken(), Integer.parseInt(stok.nextToken()));
					}
					
				} else if(firstkeyword.equals("import")) {
					//import [type] [url] [name]
					if(secondkeyword.toLowerCase().equals("table")) {
						Import imp = new Import(stok.nextToken(), stok.nextToken(), TableManager.TYPE_TABLE, tableman);
						imp.doAction();
					} else if(secondkeyword.toLowerCase().equals("procedure")){
						Import imp = new Import(stok.nextToken(), stok.nextToken(), TableManager.TYPE_PROC, tableman);
						imp.doAction();
					}else if(secondkeyword.toLowerCase().equals("schema")){
						Import imp = new Import(stok.nextToken(), stok.nextToken(), TableManager.TYPE_SCHEMA, tableman);
						imp.doAction();
					}
					
				}else if(firstkeyword.equals("adduser")){
					//need to add user permissions...
					
					//String user = secondkeyword;
					//String password = stok.nextToken().trim();
					//String skey = null;
					//if(stok.hasMoreTokens()){
					//	skey = stok.nextToken();
					//}
					
					//if the crypto libraries are in effect
					//if(tableman instanceof TableManagerCrypto){
						
					//}
					
				}else{
					throw new SQLException(firstkeyword + " is an unknown keyword or it was used incorrectly");
				}
			}
			return queryresults;
			
		}catch(Exception e){
			queryresults.reset();
			//if this is a stored procedure, try to give a hint to what line
			//number is the problem, else just show the message
			if(i>0){
				fatal=true;
				//e.printStackTrace(System.err);
				throw new AshpoolException(e.toString() + " Near Command: " + (i+1));
			}else{
				//e.printStackTrace(System.err);
				/* if( Boolean.getBoolean(((String)getGlobalVariable("SYS:DEBUG"))) ){
					e.printStackTrace(System.err);
				} */
				throw new AshpoolException(e.toString());
			}
		}
	}
	
	/** sql type query */
	public AResultSet executeStatement(String query) throws Exception{
		return executeSQLStatement(query);
	}
	
	/** Help out GC by nulling out everything we own */
	public void nullify(){
		select_filter = null;
		create_filter = null;
		insert_filter = null;
		delete_filter = null;
		update_filter = null;
		bXSL = null;
		tableman = null;
		variables = null;
		bsf = null;
	}
}
