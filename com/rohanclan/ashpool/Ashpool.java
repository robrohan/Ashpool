/*
 * Ashpool - XML Database
 * Copyright (C) 2003 Rob Rohan
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
 * Ashpool.java
 *
 * Created on February 1, 2003, 10:56 AM
 */ 

package com.rohanclan.ashpool;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.rohanclan.ashpool.cmds.About;
import com.rohanclan.ashpool.cmds.AshpoolCmd;
import com.rohanclan.ashpool.cmds.RunBSF;
import com.rohanclan.ashpool.core.AResultSet;
import com.rohanclan.ashpool.core.AResultSetMetaData;
import com.rohanclan.ashpool.core.AshpoolException;
import com.rohanclan.ashpool.core.ConnectionManager;

/**
 * This is the basic commandline DBMS for Ashpool.
 * @author  rob
 */
public class Ashpool 
{
	//the prompt
	private static final String p1 = "Ashpool~# ";
	//private static final String p2 = "Ashpool~> ";
	
	private ConnectionManager connMan;
	private boolean running = false;
	private boolean interactive = true;
	
	private Runtime r = Runtime.getRuntime();
	
	private String currentcmd="";
	//all the system commands
	public Map commands;
	//the script file that holds commands
	private String scriptfile = null;
	
	private InputStream stdin;
	private OutputStream stdout;
	
	/** Creates a new instance of Ashpool */
	public Ashpool(String datauri) 
	{	
		try
		{
			System.out.println("Datastore : [" + datauri + "]");
			File source = new File(datauri);
			if(!source.exists()){
				throw new AshpoolException(
					"The directory '" + datauri + "' doesn't exist. Please create it before pointing Ashpool to it"
				);
			}
			
			connMan = new ConnectionManager(source);
			
			//so we can see time it takes etc
			connMan.getCommandManager().setVariable("STATS","true");
			
			//build the command list
			commands = new java.util.HashMap();
			buildCommandList();
		}
		catch(Exception e)
		{
			System.err.println("Startup Error: " + e.toString());
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
	
	/** starts the main program loop */
	public void startup()
	{
		running = true;
		startMainProgramLoop();
	}
	
	/** load all the default commands */
	private void buildCommandList()
	{
		commands.put("quit",                 new Ashpool.Quit());
		commands.put("help",                 new Ashpool.Help());
		commands.put("sys",                  new Ashpool.Memory());
		commands.put("gc",                   new Ashpool.GC());
		commands.put("sql_run",              new Ashpool.RunSQL());
		commands.put("done",                 new Ashpool.Done());
		commands.put("system_run",           new Ashpool.Run());
		commands.put("echo_run",             new Ashpool.Echo());
		commands.put("about",                new About());
		try
		{
			commands.put("bsf_run",          new RunBSF(this));
			System.out.println("Scripting : [  OK  ]");
		}
		catch(Exception e)
		{
			System.out.println("Scripting : [ Fail ]");
		}
		
	}
	
	public ConnectionManager getConnectionManager()
	{
		return connMan;
	}
	
	/** set where the input and output goes */
	public void setInputOutput(InputStream in, OutputStream out)
	{
		stdin = in;
		stdout = out;
	}
	
	/** set the script file to execute */
	public void setScriptFile(String scriptfile)
	{
		this.scriptfile = scriptfile;
		interactive = false;
	}
	
	/** all should be setup now - this starts running commands or shows the 
	 * prompt for interactive mode 
	 */
	public void startMainProgramLoop()
	{
		StringBuffer commandbuff;
		String command;
		
		int in = 0;
		println(" <   Welcome to Ashpool  >");
		println("<< Type 'help;' for help >>");
		println("");
		print(p1);
		
		try
		{
			//if there in no script file provided, use System.in
			//no log file set to stdout
			if(scriptfile == null)
			{
				setInputOutput(System.in, System.out);
			}
			else
			{
				setInputOutput(new FileInputStream(scriptfile), System.out);
				logprint("Using File: [" + this.scriptfile + "]");
			}
			
			while(running)
			{
				commandbuff = new StringBuffer();
				boolean inAQuote = false;
				boolean keepreading = true;
				
				//while( (char)in != ';' && !inAQuote ){
				while(keepreading)
				{
					in = stdin.read();
					
					//check if we need to switch the in quote flag
					if((char)in == '\'')
					{
						//if in a quote get out
						if(inAQuote)
						{
							inAQuote = false;
						//if not in a quote go into a quote
						}
						else if(!inAQuote)
						{
							inAQuote = true;
						}
					}
					
					//add to the command buffer
					commandbuff.append((char)in);
					if(in == '\n' && interactive)
					{
						print(p1);
					}
					
					//if we didnt hit a ; or we did hit a ; but we are in
					//quoted text keep reading
					if( (char)in != ';' || ( (char)in == ';' && inAQuote) )
					{
						keepreading = true;
					}
					else
					{
						keepreading = false;
					}
				}
				
				//clear out the enter
				command = commandbuff.substring(0,commandbuff.length() -1);
				in = 0;
				
				//look to see if it's just a command (i.e. quit, help, etc and
				//if so handle it
				Object runner = commands.get(command.toString().trim().toLowerCase());
				if(runner != null)
				{
					((AshpoolCmd)runner).doAction();
				}
				
				//if it's not a loaded command
				//if(!handled){
				if(runner == null){
					//calling exec? (external program)
					if(command.trim().startsWith("!")){
						logprint("running: " + command.toString().trim().substring(1));
						r.exec(command.toString().trim().substring(1));
					//comment?
					//}else if(command.trim().startsWith("--")){
						//do nothing
					//do the echo command
					}else if(command.trim().toLowerCase().startsWith("echo")){
						((Ashpool.Echo)commands.get("echo_run")).setMessage(
							command.trim().substring("echo".length())
						);
						((Ashpool.Echo)commands.get("echo_run")).doAction();
					//run a BSF script
					/*}else if(command.trim().startsWith("%")){
						Object sl = commands.get("bsf_run");
						
						if(sl != null){
							((RunBSF)sl).setScript(
								command.trim().substring(1)
							);
							((RunBSF)sl).doAction();
						}*/
					//run a ashpool script
					}else if(command.trim().startsWith("@")){
						((Ashpool.Run)commands.get("system_run")).setFile(
							command.trim().substring(1)
						);
						((Ashpool.Run)commands.get("system_run")).doAction();
					//finally, try running it as an sql statement
					}else{
						currentcmd = command.toString().trim();
						((Ashpool.RunSQL)commands.get("sql_run")).doAction();
					}
				}
				////////////////////////////////////////////////////////////////
			}
		}catch(Exception e){
			System.err.println("Main loop: " + e.toString());
			e.printStackTrace(System.err);
		}
	}

	/** repeats a string x times */
	public void repeat(String pattern, int length){
		for(int x=0; x<length; x++){
			print(pattern);
		}
	}
	
	/** draws a "line" */
	public void drawLine(String pattern, int length){
		repeat(pattern, length);
		println("");
	}

	/** cause I am lazy */
	public void print(String msg){
		if(interactive){
			System.out.print(msg);
		}
	}

	/** cause I am lazy */
	public void println(String msg){
		if(interactive){
			System.out.println(msg);
		}
	}
	/** some things should not be logged, i.e. only
	 * make sence in interactive mode - but some things 
	 * should show in both contexts - those items use
	 * this function
	 */
	public void logprint(String msg){
		try{
			stdout.write((msg + "\n").getBytes());
			stdout.flush();
		}catch(Exception e){
			System.err.println("logprint: " + e.toString());
			e.printStackTrace(System.err);
		}
	}
	
	
	/////Commands//////////////////////////////////////////////////////////////
	final class Help implements AshpoolCmd{
		public Help(){;}
		
		public void doAction(){
						print("+"); repeat("=",52); println("+");
			println("| * Basic Help:                                      |");
			println("| End SQL commands with a ';' to run them.           |");
			println("| Type 'quit;' to exit                               |");
						print("+"); repeat("=",52); println("+");
			println("| * System Commands:                                 |");
			println("| select test;            = simple result            |");
			println("| select tables;          = table listing            |");
			println("| select types;           = supported types          |");
			println("| select columns [table]; = show a table's columns   |");
			println("| select env;             = show env variables       |");
			println("| select $[var];          = select an env variable   |");
			println("| set [var] = [value];    = set env variable         |");
			println("| unset [var];            = unset env variable       |");
			println("| sys;                    = see system environment   |");
			println("| gc;                     = force garbage collection |");
			println("| done;                   = turn on interactive mode |");
			println("| ![command];             = execute system command   |");
			println("| @[file];                = run commands in [file]   |");
			println("| exec [proc] [p=v], ...  = execute a stored proc    |");
			if(commands.get("bsf_run") != null){
			println("| %[BSF commands];        = runs BSF command block   |");
			}
			println("| echo [text];            = echos text to the screen |");
						print("+"); repeat("=",52); println("+");
		}
	}

	final class Memory implements AshpoolCmd{
		long freeMemory;
		long totalMemory;
		long maxMemory;
		
		public Memory(){;}
		
		public void doAction() {
			
			freeMemory  = r.freeMemory();
			totalMemory = r.totalMemory();
			maxMemory   = r.maxMemory();
			
			logprint("Available Processors: " + r.availableProcessors());
			logprint("Total: " + totalMemory 
					+ " (" + (int)(totalMemory/1024) + "K " 
					+ " " + (int)(totalMemory/1024/1024) + "MB)"
					);
			logprint("Free : " + freeMemory
					+ " (" + (int)(freeMemory/1024) + "K "
					+ " " + (int)(freeMemory/1024/1024) + "MB)"
					);
			logprint("Max  : " + maxMemory
					+ " (" + (int)(maxMemory/1024) + "K "
					+ " " + (int)(maxMemory/1024/1024) + "MB)"
					);
			 print("+"); repeat("=",51); println("+");
			logprint("SAX Factory      : "
				+ System.getProperty("javax.xml.parsers.SAXParserFactory")
			);
			
			logprint("Transform Factory: "
				+ System.getProperty("javax.xml.transform.TransformerFactory")
			);
			
			logprint("SAX Driver       : "
				+ System.getProperty("org.xml.sax.driver")
			);
			
			logprint("Runtime Version  : "
				+ System.getProperty("java.runtime.version")
			);
			logprint("OS Name          : "
				+ System.getProperty("os.name")
			);
			logprint("Processor Type   : "
				+ System.getProperty("os.arch")
			);
			logprint("File Encoding    : "
				+ System.getProperty("file.encoding")
			);
			logprint("User Language    : "
				+ System.getProperty("user.language")
			);
			
			if(commands.get("bsf_run") != null){
			logprint("Script Language  : "
				+ ((RunBSF)commands.get("bsf_run")).getLanguage());
			}
		}
	}
	
	class RunSQL implements AshpoolCmd{
		AResultSet qresults;
		public RunSQL(){;}
		
		public void doAction() throws Exception {
			boolean showstats = true;
			try{
				showstats = connMan.getCommandManager().getGlobalVariable("SYS:STATS").toString().equalsIgnoreCase("true");
			}catch(Exception e){;}
			
			try{
				long start = 0;
				if(showstats) start = System.currentTimeMillis();
				
				qresults = connMan.executeStatement(currentcmd);
				
				if(qresults != null && ((AResultSetMetaData)qresults.getMetaData()).getRecordCount() > 0){
					
					drawLine("=",80);
					int colcount = qresults.getMetaData().getColumnCount();
					
					for(int c=1; c<=colcount; c++){
						print(qresults.getMetaData().getColumnName(c) + "|");
					}
					println("");
					drawLine("-",80);
					while(qresults.next()){
						for(int c=1; c<=colcount; c++){
							print(qresults.getString(c) + "|");
						}
						println("");
					}
					drawLine("=",80);
					
					if(showstats)
						logprint(
							((AResultSetMetaData)qresults.getMetaData()).getRecordCount()
							+ " Row(s) Returned."
						);
				}else{
					if(showstats)
						logprint("0 Rows Returned.");
				}
				if(showstats){
					long qtime = System.currentTimeMillis() - start;
					logprint("Total Time: " + (qtime / 1000) + " second(s) - " + qtime + "ms");
				}
			}catch(Exception e){
				logprint("Error with command: ");
				logprint("'"  + currentcmd + "'");
				try{
					if(connMan.getCommandManager().getGlobalVariable("SYS:DEBUG").toString().equalsIgnoreCase("false")){
						logprint(e.toString());
					}else{
						e.printStackTrace(System.err);
					}
				}catch(Exception q){ /* ? */ }
			}
		}
	}
		
	class Quit implements AshpoolCmd{
		public Quit(){;}
		public void doAction() throws Exception{
			running = false;
			System.exit(0);
		}
	}
	
	class GC implements AshpoolCmd{
		public GC(){;}
		
		public void doAction() throws Exception{
			logprint("Garbage Collecting...");
			r.gc();
			logprint("[ Done ]");
		}
	}
	
	/** runs a script */
	class Run implements AshpoolCmd{
		String scriptfile;
		public Run(){;}
		
		public void doAction() throws Exception{
			try{
				interactive = false;
				setInputOutput(new FileInputStream(this.scriptfile), System.out);
			}catch(Exception e){
				interactive = true;
				System.err.println("Can not load scriptfile: " + this.scriptfile + " " + e.toString());
			}
		}
		
		public void setFile(String filename){
			if(filename.trim().startsWith("@")){
				this.scriptfile = filename.trim().substring(1);
			}else{
				this.scriptfile = filename;
			}
		}
	}
	
	/** echo command */
	class Echo implements AshpoolCmd {
		String msg="";
		public Echo(){;}
		
		public void doAction() throws Exception {
			if(msg.indexOf("$") > 0){
				msg = connMan.getCommandManager().replaceVariables(msg);
			}
			logprint(msg.trim());
		}
		
		public void setMessage(String message){
			this.msg = message;
		}
	}
	
	/** exits a script and goes to interactive mode */
	class Done implements AshpoolCmd{
		public Done(){;}
		
		public void doAction() throws Exception 
		{
			setInputOutput(System.in, System.out);
			interactive = true;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////
	public static void main(String args[])
	{
		if(args.length < 1)
		{
			System.out.println(
				"Usage: java -jar Ashpool.jar [datastore] <scriptfile>"
			);
			System.exit(1);
		}
		
		System.out.println("+===============================+");
		System.out.println("|    Ashpool  \u00ABXML Database\u00BB    |");
		System.out.println("|   Copyright 2003  Rob Rohan   |");
		System.out.println("+===============================+");
		
		Ashpool boot = new Ashpool(args[0]);
		
		if(args.length >= 2)
		{
			boot.setScriptFile(args[1]);
		}
		
		boot.startup();
	}
}
