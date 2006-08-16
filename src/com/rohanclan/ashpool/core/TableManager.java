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
 * TableManager.java
 *
 * Created on February 1, 2003, 10:15 AM
 */

package com.rohanclan.ashpool.core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** This class handles most of the IO stuff for Ashpool. For example, table moving
 * table renaming, getting an InputStream from a table, etc
 * @author Rob Rohan
 */
public class TableManager {
	/** what file extension denotes a table (.xml) */
	public static final String TABLEEXT      = ".xml";
	/** what file extension denotes a schema (.xsd) */
	public static final String TABLESCHEMA   = ".xsd";
	/** what file extension denotes a compressed table (.xmz) */
	public static final String TABLECOMPRESS = ".xmz";
	/** what file extension denotes a counter table (.icf) */
	public static final String TABLECOUNTER  = ".icf";
	/** what file extension denotes a stored procedure have (.sp) */
	public static final String STORED_PROC  = ".sp";
	
	/** reference a normal table */
	public static final byte TYPE_TABLE     = 0;
	/** reference a schema table */
	public static final byte TYPE_SCHEMA    = 1;
	/** reference a temp table */
	public static final byte TYPE_TEMPTABLE = 2;
	/** reference a counter table */
	public static final byte TYPE_COUNTER   = 3;
	/** reference a stored procedure */
	public static final byte TYPE_PROC   	= 4;
	
	protected File datastore;
	protected boolean readonly = false;
	
	public TableManager(){;}
	
	/** Creates a new instance of TableManager
	 * @param datasource the directory used as the datastore
	 * @throws Exception any startup errors
	 */
	public TableManager(File datasource) throws Exception 
	{
		setDataStore(datasource);
	}
	
	public void setDataStore(File datasource) throws Exception
	{
		if(!datasource.exists())
		{
			System.err.println("The datastore does not exist.");
			throw new Exception("The datastore does not exist: " + datasource.toString());
		}
		
		if(!datasource.isDirectory())
		{
			System.err.println("The datastore is not a directory.");
			throw new Exception("The datastore is not a directory: " + datasource.toString());
		}
		
		if(!datasource.canWrite())
		{
			System.err.println("The datastore is read only.");
			readonly = true;
		}
		
		this.datastore = datasource;
	}
	
	/** read only datastore?
	 * @return if the datastore is readonly (no wrte permissions etc)
	 */
	public boolean isReadOnly(){
		return readonly;
	}
	
	/** get the string ext from the defined byte */
	private static String extFromByte(byte type){
		switch(type){
			case TYPE_TABLE:
			case TYPE_TEMPTABLE:
				return TABLEEXT;
			case TYPE_SCHEMA:
				return TABLESCHEMA;
			case TYPE_COUNTER:
				return TABLECOUNTER;
			case TYPE_PROC:
				return STORED_PROC;
		}
		return ".unk";
	}
	
	/** looks up the next number in a serialed (autonumbered) table
	 * @return the next original number for the table
	 * @param tablename the table who owns the autonumber
	 * @throws IOException if there is a problem opening/reading the autonumber file
	 */
	public int getNextSequence(String tablename) throws Exception {
		int autonum=0;
		synchronized("GET_NEXT_SEQ_" + tablename){
			DataInputStream dis = new DataInputStream(
				getTableInputStream(tablename, TYPE_COUNTER)
			);
			
			autonum = dis.readInt();
			dis.close();
			autonum++;
			
			DataOutputStream dos = new DataOutputStream(
				getTableOutputStream(tablename, TYPE_COUNTER)
			);
			
			dos.writeInt(autonum);
			dos.flush();
			dos.close();
		}
		return autonum;
	}
	
	/** changes the base sequence number for a autonumbered table */
	public void setSequenceStart(String tablename, int base) throws Exception{
		synchronized("GET_NEXT_SEQ_" + tablename){
			DataOutputStream dos = new DataOutputStream(
				getTableOutputStream(tablename, TYPE_COUNTER)
			);
			
			dos.writeInt(base);
			dos.flush();
			dos.close();
		}
		//System.out.println("Alter."); 
	}
	
	/** create a table from a stream */
	public void createTable(String tablename, InputStream is, byte type) throws Exception{
		
		OutputStream fops = getTableOutputStream(tablename,type);
		
		///////////////////////////////////////////
		//get the proper extention
		String tabletype = extFromByte(type);
		File tFile = new File(
				getDatastoreURI() + System.getProperty("file.separator")
				+ tablename + tabletype
		);
		if(type == TableManager.TYPE_TEMPTABLE){
			tFile.deleteOnExit();
		}
		////////////////////////////////////////////
		
		//java.io.FileOutputStream fops = new java.io.FileOutputStream(tFile);
		
		StringBuffer filebuff = new StringBuffer();
		int x;
		while((x = is.read()) != -1){
			filebuff.append((char)x);
			if(filebuff.length() == 1024){
				fops.write(filebuff.toString().getBytes());
				filebuff.delete(0, filebuff.length());
			}
		}
		//last flush
		fops.write(filebuff.toString().getBytes());
		
		fops.flush();
		fops.close();
		is.close();
	}
	
	/** renames a table (and if the table has a counter and or schema file
	 * renames those as well).
	 * @param oldname the current name
	 * @param newname the name the table should be moved to
	 * @throws IOException if anything goes wrong...
	 */
	public void renameTable(String oldname, String newname) throws IOException{
		java.io.File old = getTableFile(oldname, TYPE_TABLE);
		//move the schema and the sequence too
		if(hasSchema(oldname)){
			java.io.File olds = getTableFile(oldname,TYPE_SCHEMA);
			olds.renameTo(getTableFile(newname,TYPE_SCHEMA));
		}
		if(hasSequence(oldname)){
			java.io.File olds = getTableFile(oldname,TYPE_COUNTER);
			olds.renameTo(getTableFile(newname,TYPE_COUNTER));
		}
		old.renameTo(getTableFile(newname,TYPE_TABLE));
	}
	
	/** create a new table from a string */
	public void createTable(String tablename, String Data, byte type) throws Exception {
		createTable(tablename, new ByteArrayInputStream(Data.getBytes()), type);
	}
	
	/** get the datastore (the directory) as a file object */
	public File getDatastore(){
		return this.datastore;
	}
	
	/** get the datastore (the directory) as a String */
	public String getDatastoreURI(){
		return datastore.getPath().toString();
	}
	
	/** the base method for getting an output stream to a file in the datastore
	 * can be any type (TABLE, PROCDEURE, etc)
	 */
	public OutputStream getTableOutputStream(String tablename, byte type) throws IOException, Exception{
		FileOutputStream fos = new FileOutputStream(
			getTableFullPath(tablename, type)
		);
		
		return fos;
	}
	
	/** get a table as an input stream */
	public InputStream getTableInputStream(String tablename) throws Exception {
		return getTableInputStream(tablename, TableManager.TYPE_TABLE);
	}
	
	/** get a schema as an input stream */
	public InputStream getSchemaInputStream(String tablename) throws Exception {
		return getTableInputStream(tablename, TableManager.TYPE_SCHEMA);
	}
	
	/** get a stored procedure as an input stream */
	public InputStream getProcedureInputStream(String procname) throws Exception {
		return getTableInputStream(procname, TYPE_PROC);
		//return new java.io.FileInputStream(getTableFullPath(procname, TYPE_PROC));
	}
	
	
	/** 
	 * the base method for getting an input stream to a file in the datastore
	 * can be any type (TABLE, PROCDEURE, etc)
	 * get a table (xml file) as an input stream 
	 */
	public InputStream getTableInputStream(String tablename, byte type) throws IOException, Exception {
		FileInputStream fis = new FileInputStream(
			getTableFullPath(tablename, type)
		);
		
		return fis;
	}
	
	/** get a table (xml file) as a file object */
	public File getTableFile(String tablename, byte type){  
		return new File(getTableFullPath(tablename, type));
	}

	/** get a table (xml file) as a string path */
	public String getTableFullPath(String tablename, byte type){
		switch(type){
			case TableManager.TYPE_TABLE:
			case TableManager.TYPE_TEMPTABLE:
				return getDatastoreURI() + System.getProperty("file.separator") + tablename + TABLEEXT;
			case TableManager.TYPE_SCHEMA:
				return getDatastoreURI() + System.getProperty("file.separator") + tablename + TABLESCHEMA;
			case TableManager.TYPE_COUNTER:
				return getDatastoreURI() + System.getProperty("file.separator") + tablename + TABLECOUNTER;
			case TableManager.TYPE_PROC:
				return getDatastoreURI() + System.getProperty("file.separator") + tablename + STORED_PROC;
		}
		
		return "thisfiledoesntexist";
	}
	
	/* return true if the passed name is a table */
	/** Returns true if the passed table exists in the datastore. <b>Note:</b> checks
	 * for <i>table</i> only (not schema, etc)
	 * @return true if table is in the datastore
	 * @param tablename the table to look for
	 */
	public boolean isTable(String tablename){
		return getTableFile(tablename, TableManager.TYPE_TABLE).exists();
	}
	
	/** returns the (possible) stored procedures in this datastore
	 * @param rs the record set to fill with the found procedure information
	 */
	public void getProcedures(AResultSet rs){
		//Only procedure descriptions matching the schema and
		//procedure name criteria are returned.  They are ordered by
		//<code>PROCEDURE_SCHEM</code> and <code>PROCEDURE_NAME</code>.
		// <P>Each procedure description has the the following columns:
		// <OL>
		//<LI><B>PROCEDURE_CAT</B> String => procedure catalog (may be <code>null</code>)
		//LI><B>PROCEDURE_SCHEM</B> String => procedure schema (may be <code>null</code>)
		//<LI><B>PROCEDURE_NAME</B> String => procedure name
		//<LI> reserved for future use
		//<LI> reserved for future use
		//<LI> reserved for future use
		//<LI><B>REMARKS</B> String => explanatory comment on the procedure
		//<LI><B>PROCEDURE_TYPE</B> short => kind of procedure:
		//    <UL>
		//    <LI> procedureResultUnknown - May return a result
		//    <LI> procedureNoResult - Does not return a result
		//   <LI> procedureReturnsResult - Returns a result
		//    </UL>
		//</OL>
		
		List<Object> vProc  = new ArrayList<Object>();
		List<Object> vRemark = new ArrayList<Object>();
		List<Object> vType   = new ArrayList<Object>();
		List<Object> vNull   = new ArrayList<Object>();
		
		File list[] = getFileTables();
		
		int listlen = list.length;
		for(int r=0; r<listlen; r++){
			if(list[r].getName().toLowerCase().endsWith(STORED_PROC)){
				vProc.add(
					list[r].getName().substring(
						0, list[r].getName().length() - STORED_PROC.length()
					)
				);
				vRemark.add("none yet");
				vType.add("0");
				vNull.add("");
			}
		}
		
		rs.addColumn("PROCEDURE_CAT", 	vNull, 		Types.VARCHAR);
		rs.addColumn("PROCEDURE_SCHEM", vNull, 		Types.VARCHAR);
		rs.addColumn("PROCEDURE_NAME", 	vProc, 		Types.VARCHAR);
		rs.addColumn("REMARKS", 		vRemark,	Types.VARCHAR);
		rs.addColumn("PROCEDURE_TYPE",	vType, 		Types.SMALLINT);
	}
	
	/** returns the (possible) tables in this datastore
	 * @param rs the record set to fill with the found table information
	 */
	public void getTables(AResultSet rs){
		getTables(rs, null);
	}
	
	
	public void getTables(AResultSet rs, String[] types){
		//TABLE_CAT    String => table catalog (may be <code>null</code>)
		//TABLE_SCHEM  String => table schema (may be <code>null</code>)
		//TABLE_NAME   String => table name
		//TABLE_TYPE   String => table type.  Typical types are "TABLE",
		//      	"VIEW",	"SYSTEM TABLE", "GLOBAL TEMPORARY",
		//              "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
		//REMARKS      String => explanatory comment on the table
		//TYPE_CAT     String => the types catalog (may be <code>null</code>)
		//TYPE_SCHEM   String => the types schema (may be <code>null</code>)
		//TYPE_NAME    String => type name (may be <code>null</code>)
		//SELF_REFERENCING_COL_NAME  String => name of the designated
		//             "identifier" column of a typed table (may be <code>null</code>)
		//REF_GENERATION
		
		//String validtable = "X";
		
		List<Object> getTypes = new ArrayList<Object>();
		if(types != null){
			for(int i = 0; i < types.length; i++){
				getTypes.add(types[i]);
			}
		}
		
		List<Object> vTable  = new ArrayList<Object>();
		List<Object> vSchema = new ArrayList<Object>();
		List<Object> vName   = new ArrayList<Object>();
		List<Object> vSize   = new ArrayList<Object>();
		List<Object> vNull   = new ArrayList<Object>();
		
		File list[] = getFileTables();
		
		int listlen = list.length;
		for(int r=0; r<listlen; r++){
			String fname = list[r].getName();
			
			if((fname.toLowerCase().endsWith(TABLEEXT) || fname.toLowerCase().endsWith(TABLECOMPRESS))
				&& !fname.startsWith("!")
			){
				//this is bad :(
				///////////////////////////////////////////////////////////
				if(fname.startsWith("sys_") && (getTypes.contains("SYSTEM TABLE") || types == null)){
					vName.add(
						fname.substring(
							0, fname.length() - TABLEEXT.length()
						)
					);
					vTable.add("SYSTEM TABLE");
					vSize.add("File Size: " + list[r].length()  + " bytes (" + (list[r].length() / 1024) + "K)");
					vNull.add("");
					
					//if there is a schema for this table add it to the list
					if(new java.io.File(getDatastoreURI() + System.getProperty("file.separator")
						+ fname.substring(0, fname.length() - TABLEEXT.length())
						+ TABLESCHEMA).exists()){
							
						vSchema.add(fname.substring(0, fname.length() - TABLEEXT.length()));
					}else{
						vSchema.add("");
					}
				}
				////////////////////////////////////////////////////////////
				else if(fname.startsWith("~") 
					&& (getTypes.contains("LOCAL TEMPORARY") || types == null)){
					vName.add(
						fname.substring(
							0, list[r].getName().length() - TABLEEXT.length()
						)
					);
					vTable.add("LOCAL TEMPORARY");
					vSize.add("File Size: " + list[r].length()  + " bytes (" + (list[r].length() / 1024) + "K)");
					vNull.add("");
					
					//if there is a schema for this table add it to the list
					if(new java.io.File(getDatastoreURI() + System.getProperty("file.separator")
						+ fname.substring(0, fname.length() - TABLEEXT.length())
						+ TABLESCHEMA).exists()){
							
						vSchema.add(fname.substring(0, fname.length() - TABLEEXT.length()));
					}else{
						vSchema.add("");
					}
				}
				////////////////////////////////////////////////////////////
				else if(
					(!fname.startsWith("~")  && !fname.startsWith("sys_"))
					&& (getTypes.contains("TABLE") || types == null)){
					vName.add(
						fname.substring(
							0, fname.length() - TABLEEXT.length()
						)
					);
					vTable.add("TABLE");
					vSize.add("File Size: " + list[r].length()  + " bytes (" + (list[r].length() / 1024) + "K)");
					vNull.add("");
					
					//if there is a schema for this table add it to the list
					if(new java.io.File(getDatastoreURI() + System.getProperty("file.separator")
						+ fname.substring(0, fname.length() - TABLEEXT.length())
						+ TABLESCHEMA).exists()){
							
						vSchema.add(fname.substring(0, fname.length() - TABLEEXT.length()));
					}else{
						vSchema.add("");
					}
				}
			}
		}
		
		rs.addColumn("TABLE_CAT",                 vNull,   java.sql.Types.VARCHAR);
		rs.addColumn("TABLE_SCHEM",               vSchema, java.sql.Types.VARCHAR);
		rs.addColumn("TABLE_NAME",                vName,   java.sql.Types.VARCHAR);
		rs.addColumn("TABLE_TYPE",                vTable,  java.sql.Types.VARCHAR);
		rs.addColumn("REMARKS",                   vSize,   java.sql.Types.VARCHAR);
		rs.addColumn("TYPE_CAT",                  vNull,   java.sql.Types.VARCHAR);
		rs.addColumn("TYPE_SCHEM",                vNull,   java.sql.Types.VARCHAR);
		rs.addColumn("TYPE_NAME",                 vNull,   java.sql.Types.VARCHAR);
		rs.addColumn("SELF_REFERENCING_COL_NAME", vNull,   java.sql.Types.VARCHAR);
		rs.addColumn("REF_GENERATION",            vNull,   java.sql.Types.VARCHAR);
	}
	
	
	/** get a file array of tables */
	public File[] getFileTables(){
		File filelist[] = null;
		try{
			
			filelist = datastore.listFiles();
			
		}catch(Exception e){
			System.err.println("Can not list tables: " + e.toString());
			//e.printStackTrace(System.err);
		}
		
		//return tables;
		return filelist;
	}

	/** returns true if the passed table has a schema */
	public boolean hasSchema(String tablename){
		return getTableFile(tablename, TableManager.TYPE_SCHEMA).exists();
	}
	
	/** returns true if the passed table has a sequence file */
	public boolean hasSequence(String tablename){
		return getTableFile(tablename, TableManager.TYPE_COUNTER).exists();
	}
	
	public void doDropDBObject(String name, byte type) throws Exception {
		File fhndl = getTableFile(name, type);
		
		if(!fhndl.exists()){
			throw new Exception("Object doesn't exist.");
		}
		if(fhndl.isDirectory()){
			throw new Exception("Object is not a file - it's a directory.");
		}
		if(!fhndl.canRead() || !fhndl.canWrite()){
			throw new Exception("Can not read or write to the Object");
		}
		if(!fhndl.delete()){
			fhndl.deleteOnExit();
			throw new Exception("Failed to drop the object for some file system reason. Will try to delete on exit.");
		}
		
	}
	
	
	public void doDropProcedure(String procname) throws Exception {
		doDropDBObject(procname,TableManager.TYPE_PROC);
	}
	
	/** 
	* drop a table from the datastore, and try to drop the schema too 
	**/
	public void doDropTable(String tablename) throws Exception {
		doDropDBObject(tablename, TableManager.TYPE_TABLE);
		if(hasSchema(tablename)) doDropDBObject(tablename, TableManager.TYPE_SCHEMA);
		if(hasSequence(tablename)) doDropDBObject(tablename, TableManager.TYPE_COUNTER);
	}
}
