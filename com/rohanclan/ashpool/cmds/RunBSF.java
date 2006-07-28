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
 * RunBSF.java
 *
 * Created on March 20, 2003, 1:21 PM
 */

package com.rohanclan.ashpool.cmds;

import org.apache.bsf.BSFManager;

import com.rohanclan.ashpool.Ashpool;
import com.rohanclan.ashpool.core.CommandManager;
/**
 *
 * @author  rob
 */
public class RunBSF implements AshpoolCmd {
	Object mgr;
	String command="";
	//String language="javascript";
	String language="ruby";
	String id="script_code";

	
	/** Creates a new instance of RunBSF */
	public RunBSF(Ashpool dbms) throws Exception 
	{
		startUp(dbms, null);
	}
	
	public RunBSF(CommandManager cmdmgr) throws Exception 
	{
		startUp(null, cmdmgr);
	}
	
	/** try to startup the bsf engine and setup the default beans */
	private void startUp(Ashpool dbms, CommandManager cmdmgr) throws Exception 
	{
		//fire up the scripting engine
		Class loader = Class.forName("org.apache.bsf.BSFManager");
		mgr = loader.newInstance();
		
		//BSFManager.registerScriptingEngine("ruby", "org.jruby.javasupport.bsf.JRubyEngine", new String[] { "rb" });
		
		//if they pass in a dbms use that to get the cmdmgr
		//(used in the Ashpool dbms)
		if(dbms != null)
		{
			cmdmgr = dbms.getConnectionManager().getCommandManager();
			((BSFManager)mgr).declareBean("dbms", dbms, dbms.getClass());
		}
		
		if(cmdmgr != null)
		{
			((BSFManager)mgr).declareBean("cmd", cmdmgr, cmdmgr.getClass());
		}
	}
	
	public void doAction() throws Exception
	{
		((BSFManager)mgr).exec(language, id, 0, 0, command);
	}
	
	public void setScript(String script)
	{
		this.command = script;
	}
	
	public void setLanguage(String language)
	{
		this.language = language;
	}
	
	public String getLanguage()
	{
		return this.language;
	}
}