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
 * Connection.java
 *
 * Created on January 30, 2003, 7:17 PM
 */
 
package com.rohanclan.ashpool.jdbc;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.rohanclan.ashpool.core.AResultSet;
import com.rohanclan.ashpool.core.AResultSetMetaData;
import com.rohanclan.ashpool.core.ConnectionManager;
 
/**
 *
 * @author  rob
 */
public class Connection implements java.sql.Connection {
	
	private ConnectionManager conMan;
	
	/** Creates a new instance of Connection */
	public Connection() {;}
	
	public Connection(File datastore, Properties info) throws SQLException
	{
		try
		{
			conMan = new ConnectionManager(datastore);
						
			//check to see if this database managed (i.e. has the system tables
			//for login and such
			AResultSet ar = conMan.executeStatement("select tables");
			boolean isManaged = false;
			
			while(ar.next())
			{
				if(ar.getString("TABLE_NAME").equals("sys_users"))
				{
					isManaged = true;
					break;
				}
			}
			
			//this database is managed so we need to check to see if this
			//user passes muster...
			if(isManaged)
			{
				if(info != null)
				{
					String username = (String)info.getProperty("user");
					if(username == null || username.length() == 0)
					{
						throw new SQLException("No 'user' passed to a managed datasource");
					}
					
					String password = (String)info.getProperty("password");
					if(password == null || password.length() == 0)
					{
						throw new SQLException("No 'password' passed to a managed datasource");
					}
					
					ar = conMan.executeStatement(
						"select key from sys_users where uname='" + username
						+ "' and password = '" + password + "'"
					);
					
					if( ((AResultSetMetaData)ar.getMetaData()).getRecordCount() == 0)
					{
						throw new SQLException("Bad user name and / or password");
					}
				}
				else
				{
					throw new SQLException("No user and password given on a managed datastore");
				}
			}
		}
		catch(Exception e)
		{
			throw new SQLException(e.getMessage());
			//System.err.println(e.toString());
		}
		
		//everything went well
	}
	
	public ConnectionManager getConnectionManager()
	{
		return conMan;
	}
	
	/** Clears all warnings reported for this <code>Connection</code> object.
	 * After a call to this method, the method <code>getWarnings</code>
	 * returns <code>null</code> until a new warning is
	 * reported for this <code>Connection</code> object.
	 *
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void clearWarnings() throws SQLException {;}
	
	/** Releases this <code>Connection</code> object's database and JDBC resources
	 * immediately instead of waiting for them to be automatically released.
	 * <P>
	 * Calling the method <code>close</code> on a <code>Connection</code>
	 * object that is already closed is a no-op.
	 * <P>
	 * <B>Note:</B> A <code>Connection</code> object is automatically
	 * closed when it is garbage collected. Certain fatal errors also
	 * close a <code>Connection</code> object.
	 *
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public void close() throws SQLException {;}
	
	/** Makes all changes made since the previous
	 * commit/rollback permanent and releases any database locks
	 * currently held by this <code>Connection</code> object.
	 * This method should be
	 * used only when auto-commit mode has been disabled.
	 *
	 * @exception SQLException if a database access error occurs or this
	 *            <code>Connection</code> object is in auto-commit mode
	 * @see #setAutoCommit
	 *
	 */
	public void commit() throws SQLException {;}
	
	/** Creates a <code>Statement</code> object for sending
	 * SQL statements to the database.
	 * SQL statements without parameters are normally
	 * executed using <code>Statement</code> objects. If the same SQL statement
	 * is executed many times, it may be more efficient to use a
	 * <code>PreparedStatement</code> object.
	 * <P>
	 * Result sets created using the returned <code>Statement</code>
	 * object will by default be type <code>TYPE_FORWARD_ONLY</code>
	 * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
	 *
	 * @return a new default <code>Statement</code> object
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public java.sql.Statement createStatement() throws SQLException {
		com.rohanclan.ashpool.jdbc.Statement stmt = new com.rohanclan.ashpool.jdbc.Statement();
		stmt.setConnectionManager(conMan);
		
		return stmt;
	}
	
	/** Creates a <code>Statement</code> object that will generate
	 * <code>ResultSet</code> objects with the given type and concurrency.
	 * This method is the same as the <code>createStatement</code> method
	 * above, but it allows the default result set
	 * type and concurrency to be overridden.
	 *
	 * @param resultSetType a result set type; one of
	 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @param resultSetConcurrency a concurrency type; one of
	 *        <code>ResultSet.CONCUR_READ_ONLY</code> or
	 *        <code>ResultSet.CONCUR_UPDATABLE</code>
	 * @return a new <code>Statement</code> object that will generate
	 *         <code>ResultSet</code> objects with the given type and
	 *         concurrency
	 * @exception SQLException if a database access error occurs
	 *         or the given parameters are not <code>ResultSet</code>
	 *         constants indicating type and concurrency
	 * @since 1.2
	 *
	 */
	public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return createStatement();
	}
	
	/** Creates a <code>Statement</code> object that will generate
	 * <code>ResultSet</code> objects with the given type, concurrency,
	 * and holdability.
	 * This method is the same as the <code>createStatement</code> method
	 * above, but it allows the default result set
	 * type, concurrency, and holdability to be overridden.
	 *
	 * @param resultSetType one of the following <code>ResultSet</code>
	 *        constants:
	 *         <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *         <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @param resultSetConcurrency one of the following <code>ResultSet</code>
	 *        constants:
	 *         <code>ResultSet.CONCUR_READ_ONLY</code> or
	 *         <code>ResultSet.CONCUR_UPDATABLE</code>
	 * @param resultSetHoldability one of the following <code>ResultSet</code>
	 *        constants:
	 *         <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
	 *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
	 * @return a new <code>Statement</code> object that will generate
	 *         <code>ResultSet</code> objects with the given type,
	 *         concurrency, and holdability
	 * @exception SQLException if a database access error occurs
	 *            or the given parameters are not <code>ResultSet</code>
	 *            constants indicating type, concurrency, and holdability
	 * @see ResultSet
	 * @since 1.4
	 *
	 */
	public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		//System.err.println(resultSetType + " " + resultSetConcurrency + " " + resultSetHoldability);
		return null;
		//return createStatement();
	}
	
	/** Retrieves the current auto-commit mode for this <code>Connection</code>
	 * object.
	 *
	 * @return the current state of this <code>Connection</code> object's
	 *         auto-commit mode
	 * @exception SQLException if a database access error occurs
	 * @see #setAutoCommit
	 *
	 */
	public boolean getAutoCommit() throws SQLException {
		return true;
	}
	
	/** Retrieves this <code>Connection</code> object's current catalog name.
	 *
	 * @return the current catalog name or <code>null</code> if there is none
	 * @exception SQLException if a database access error occurs
	 * @see #setCatalog
	 *
	 */
	public String getCatalog() throws SQLException {
		return null;
	}
	
	/** Retrieves the current holdability of <code>ResultSet</code> objects
	 * created using this <code>Connection</code> object.
	 *
	 * @return the holdability, one of
	 *        <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
	 *        <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
	 * @throws SQLException if a database access occurs
	 * @see #setHoldability
	 * @see ResultSet
	 * @since 1.4
	 *
	 */
	public int getHoldability() throws SQLException {
		//return ResultSet.HOLD_CURSORS_OVER_COMMIT;
		return ResultSet.CLOSE_CURSORS_AT_COMMIT;
	}
	
	/** Retrieves a <code>DatabaseMetaData</code> object that contains
	 * metadata about the database to which this
	 * <code>Connection</code> object represents a connection.
	 * The metadata includes information about the database's
	 * tables, its supported SQL grammar, its stored
	 * procedures, the capabilities of this connection, and so on.
	 *
	 * @return a <code>DatabaseMetaData</code> object for this
	 *         <code>Connection</code> object
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return new MetaData(this);
	}
	
	/** Retrieves this <code>Connection</code> object's current
	 * transaction isolation level.
	 *
	 * @return the current transaction isolation level, which will be one
	 *         of the following constants:
	 *        <code>Connection.TRANSACTION_READ_UNCOMMITTED</code>,
	 *        <code>Connection.TRANSACTION_READ_COMMITTED</code>,
	 *        <code>Connection.TRANSACTION_REPEATABLE_READ</code>,
	 *        <code>Connection.TRANSACTION_SERIALIZABLE</code>, or
	 *        <code>Connection.TRANSACTION_NONE</code>.
	 * @exception SQLException if a database access error occurs
	 * @see #setTransactionIsolation
	 *
	 */
	public int getTransactionIsolation() throws SQLException {
		return Connection.TRANSACTION_NONE;
	}
	
	/** Retrieves the <code>Map</code> object associated with this
	 * <code>Connection</code> object.
	 * Unless the application has added an entry, the type map returned
	 * will be empty.
	 *
	 * @return the <code>java.util.Map</code> object associated
	 *         with this <code>Connection</code> object
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 * @see #setTypeMap
	 *
	 */
	@SuppressWarnings("unchecked")
	public Map getTypeMap() throws SQLException {
		return new HashMap();
		//return null;
	}
	
	/** Retrieves the first warning reported by calls on this
	 * <code>Connection</code> object.  If there is more than one
	 * warning, subsequent warnings will be chained to the first one
	 * and can be retrieved by calling the method
	 * <code>SQLWarning.getNextWarning</code> on the warning
	 * that was retrieved previously.
	 * <P>
	 * This method may not be
	 * called on a closed connection; doing so will cause an
	 * <code>SQLException</code> to be thrown.
	 *
	 * <P><B>Note:</B> Subsequent warnings will be chained to this
	 * SQLWarning.
	 *
	 * @return the first <code>SQLWarning</code> object or <code>null</code>
	 *         if there are none
	 * @exception SQLException if a database access error occurs or
	 *            this method is called on a closed connection
	 * @see SQLWarning
	 *
	 */
	public SQLWarning getWarnings() throws SQLException {
		//return new SQLWarning();
		return null;
	}
	
	/** Retrieves whether this <code>Connection</code> object has been
	 * closed.  A connection is closed if the method <code>close</code>
	 * has been called on it or if certain fatal errors have occurred.
	 * This method is guaranteed to return <code>true</code> only when
	 * it is called after the method <code>Connection.close</code> has
	 * been called.
	 * <P>
	 * This method generally cannot be called to determine whether a
	 * connection to a database is valid or invalid.  A typical client
	 * can determine that a connection is invalid by catching any
	 * exceptions that might be thrown when an operation is attempted.
	 *
	 * @return <code>true</code> if this <code>Connection</code> object
	 *         is closed; <code>false</code> if it is still open
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isClosed() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this <code>Connection</code>
	 * object is in read-only mode.
	 *
	 * @return <code>true</code> if this <code>Connection</code> object
	 *         is read-only; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isReadOnly() throws SQLException {
		return conMan.getTableManager().isReadOnly();
	}
	
	/** Converts the given SQL statement into the system's native SQL grammar.
	 * A driver may convert the JDBC SQL grammar into its system's
	 * native SQL grammar prior to sending it. This method returns the
	 * native form of the statement that the driver would have sent.
	 *
	 * @param sql an SQL statement that may contain one or more '?'
	 * parameter placeholders
	 * @return the native form of this statement
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String nativeSQL(String sql) throws SQLException {
		return "";
	}
	
	/** Creates a <code>CallableStatement</code> object for calling
	 * database stored procedures.
	 * The <code>CallableStatement</code> object provides
	 * methods for setting up its IN and OUT parameters, and
	 * methods for executing the call to a stored procedure.
	 *
	 * <P><B>Note:</B> This method is optimized for handling stored
	 * procedure call statements. Some drivers may send the call
	 * statement to the database when the method <code>prepareCall</code>
	 * is done; others
	 * may wait until the <code>CallableStatement</code> object
	 * is executed. This has no
	 * direct effect on users; however, it does affect which method
	 * throws certain SQLExceptions.
	 * <P>
	 * Result sets created using the returned <code>CallableStatement</code>
	 * object will by default be type <code>TYPE_FORWARD_ONLY</code>
	 * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
	 *
	 * @param sql an SQL statement that may contain one or more '?'
	 * parameter placeholders. Typically this  statement is a JDBC
	 * function call escape string.
	 * @return a new default <code>CallableStatement</code> object containing the
	 * pre-compiled SQL statement
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
		return null;
	}
	
	/** Creates a <code>CallableStatement</code> object that will generate
	 * <code>ResultSet</code> objects with the given type and concurrency.
	 * This method is the same as the <code>prepareCall</code> method
	 * above, but it allows the default result set
	 * type and concurrency to be overridden.
	 *
	 * @param sql a <code>String</code> object that is the SQL statement to
	 *            be sent to the database; may contain on or more ? parameters
	 * @param resultSetType a result set type; one of
	 *         <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *         <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @param resultSetConcurrency a concurrency type; one of
	 *         <code>ResultSet.CONCUR_READ_ONLY</code> or
	 *         <code>ResultSet.CONCUR_UPDATABLE</code>
	 * @return a new <code>CallableStatement</code> object containing the
	 * pre-compiled SQL statement that will produce <code>ResultSet</code>
	 * objects with the given type and concurrency
	 * @exception SQLException if a database access error occurs
	 *         or the given parameters are not <code>ResultSet</code>
	 *         constants indicating type and concurrency
	 * @since 1.2
	 *
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}
	
	/** Creates a <code>CallableStatement</code> object that will generate
	 * <code>ResultSet</code> objects with the given type and concurrency.
	 * This method is the same as the <code>prepareCall</code> method
	 * above, but it allows the default result set
	 * type, result set concurrency type and holdability to be overridden.
	 *
	 * @param sql a <code>String</code> object that is the SQL statement to
	 *            be sent to the database; may contain on or more ? parameters
	 * @param resultSetType one of the following <code>ResultSet</code>
	 *        constants:
	 *         <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *         <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @param resultSetConcurrency one of the following <code>ResultSet</code>
	 *        constants:
	 *         <code>ResultSet.CONCUR_READ_ONLY</code> or
	 *         <code>ResultSet.CONCUR_UPDATABLE</code>
	 * @param resultSetHoldability one of the following <code>ResultSet</code>
	 *        constants:
	 *         <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
	 *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
	 * @return a new <code>CallableStatement</code> object, containing the
	 *         pre-compiled SQL statement, that will generate
	 *         <code>ResultSet</code> objects with the given type,
	 *         concurrency, and holdability
	 * @exception SQLException if a database access error occurs
	 *            or the given parameters are not <code>ResultSet</code>
	 *            constants indicating type, concurrency, and holdability
	 * @see ResultSet
	 * @since 1.4
	 *
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return null;
	}
	
	/** Creates a <code>PreparedStatement</code> object for sending
	 * parameterized SQL statements to the database.
	 * <P>
	 * A SQL statement with or without IN parameters can be
	 * pre-compiled and stored in a <code>PreparedStatement</code> object. This
	 * object can then be used to efficiently execute this statement
	 * multiple times.
	 *
	 * <P><B>Note:</B> This method is optimized for handling
	 * parametric SQL statements that benefit from precompilation. If
	 * the driver supports precompilation,
	 * the method <code>prepareStatement</code> will send
	 * the statement to the database for precompilation. Some drivers
	 * may not support precompilation. In this case, the statement may
	 * not be sent to the database until the <code>PreparedStatement</code>
	 * object is executed.  This has no direct effect on users; however, it does
	 * affect which methods throw certain <code>SQLException</code> objects.
	 * <P>
	 * Result sets created using the returned <code>PreparedStatement</code>
	 * object will by default be type <code>TYPE_FORWARD_ONLY</code>
	 * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
	 *
	 * @param sql an SQL statement that may contain one or more '?' IN
	 * parameter placeholders
	 * @return a new default <code>PreparedStatement</code> object containing the
	 * pre-compiled SQL statement
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		//System.err.println("prepareStatement - Don't know how yet: " + sql);
		return null;
	}
	
	/** Creates a default <code>PreparedStatement</code> object capable
	 * of returning the auto-generated keys designated by the given array.
	 * This array contains the names of the columns in the target
	 * table that contain the auto-generated keys that should be returned.
	 * This array is ignored if the SQL
	 * statement is not an <code>INSERT</code> statement.
	 * <P>
	 * An SQL statement with or without IN parameters can be
	 * pre-compiled and stored in a <code>PreparedStatement</code> object. This
	 * object can then be used to efficiently execute this statement
	 * multiple times.
	 * <P>
	 * <B>Note:</B> This method is optimized for handling
	 * parametric SQL statements that benefit from precompilation. If
	 * the driver supports precompilation,
	 * the method <code>prepareStatement</code> will send
	 * the statement to the database for precompilation. Some drivers
	 * may not support precompilation. In this case, the statement may
	 * not be sent to the database until the <code>PreparedStatement</code>
	 * object is executed.  This has no direct effect on users; however, it does
	 * affect which methods throw certain SQLExceptions.
	 * <P>
	 * Result sets created using the returned <code>PreparedStatement</code>
	 * object will by default be type <code>TYPE_FORWARD_ONLY</code>
	 * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
	 *
	 * @param sql an SQL statement that may contain one or more '?' IN
	 *        parameter placeholders
	 * @param columnNames an array of column names indicating the columns
	 *        that should be returned from the inserted row or rows
	 * @return a new <code>PreparedStatement</code> object, containing the
	 *         pre-compiled statement, that is capable of returning the
	 *         auto-generated keys designated by the given array of column
	 *         names
	 * @exception SQLException if a database access error occurs
	 *
	 * @since 1.4
	 *
	 */
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return null;
	}
	
	/** Creates a default <code>PreparedStatement</code> object that has
	 * the capability to retrieve auto-generated keys. The given constant
	 * tells the driver whether it should make auto-generated keys
	 * available for retrieval.  This parameter is ignored if the SQL
	 * statement is not an <code>INSERT</code> statement.
	 * <P>
	 * <B>Note:</B> This method is optimized for handling
	 * parametric SQL statements that benefit from precompilation. If
	 * the driver supports precompilation,
	 * the method <code>prepareStatement</code> will send
	 * the statement to the database for precompilation. Some drivers
	 * may not support precompilation. In this case, the statement may
	 * not be sent to the database until the <code>PreparedStatement</code>
	 * object is executed.  This has no direct effect on users; however, it does
	 * affect which methods throw certain SQLExceptions.
	 * <P>
	 * Result sets created using the returned <code>PreparedStatement</code>
	 * object will by default be type <code>TYPE_FORWARD_ONLY</code>
	 * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
	 *
	 * @param sql an SQL statement that may contain one or more '?' IN
	 *        parameter placeholders
	 * @param autoGeneratedKeys a flag indicating whether auto-generated keys
	 *        should be returned; one of the following <code>Statement</code>
	 *        constants:
	 * @param autoGeneratedKeys a flag indicating that auto-generated keys should be returned, one of
	 *        <code>Statement.RETURN_GENERATED_KEYS</code> or
	 * 	      <code>Statement.NO_GENERATED_KEYS</code>.
	 * @return a new <code>PreparedStatement</code> object, containing the
	 *         pre-compiled SQL statement, that will have the capability of
	 *         returning auto-generated keys
	 * @exception SQLException if a database access error occurs
	 *         or the given parameter is not a <code>Statement</code>
	 *         constant indicating whether auto-generated keys should be
	 *         returned
	 * @since 1.4
	 *
	 */
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return null;
	}
	
	/** Creates a default <code>PreparedStatement</code> object capable
	 * of returning the auto-generated keys designated by the given array.
	 * This array contains the indexes of the columns in the target
	 * table that contain the auto-generated keys that should be made
	 * available. This array is ignored if the SQL
	 * statement is not an <code>INSERT</code> statement.
	 * <P>
	 * An SQL statement with or without IN parameters can be
	 * pre-compiled and stored in a <code>PreparedStatement</code> object. This
	 * object can then be used to efficiently execute this statement
	 * multiple times.
	 * <P>
	 * <B>Note:</B> This method is optimized for handling
	 * parametric SQL statements that benefit from precompilation. If
	 * the driver supports precompilation,
	 * the method <code>prepareStatement</code> will send
	 * the statement to the database for precompilation. Some drivers
	 * may not support precompilation. In this case, the statement may
	 * not be sent to the database until the <code>PreparedStatement</code>
	 * object is executed.  This has no direct effect on users; however, it does
	 * affect which methods throw certain SQLExceptions.
	 * <P>
	 * Result sets created using the returned <code>PreparedStatement</code>
	 * object will by default be type <code>TYPE_FORWARD_ONLY</code>
	 * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
	 *
	 * @param sql an SQL statement that may contain one or more '?' IN
	 *        parameter placeholders
	 * @param columnIndexes an array of column indexes indicating the columns
	 *        that should be returned from the inserted row or rows
	 * @return a new <code>PreparedStatement</code> object, containing the
	 *         pre-compiled statement, that is capable of returning the
	 *         auto-generated keys designated by the given array of column
	 *         indexes
	 * @exception SQLException if a database access error occurs
	 *
	 * @since 1.4
	 *
	 */
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return null;
	}
	
	/**
	 * Creates a <code>PreparedStatement</code> object that will generate
	 * <code>ResultSet</code> objects with the given type and concurrency.
	 * This method is the same as the <code>prepareStatement</code> method
	 * above, but it allows the default result set
	 * type and concurrency to be overridden.
	 *
	 * @param sql a <code>String</code> object that is the SQL statement to
	 *            be sent to the database; may contain one or more ? IN
	 *            parameters
	 * @param resultSetType a result set type; one of
	 *         <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *         <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @param resultSetConcurrency a concurrency type; one of
	 *         <code>ResultSet.CONCUR_READ_ONLY</code> or
	 *         <code>ResultSet.CONCUR_UPDATABLE</code>
	 * @return a new PreparedStatement object containing the
	 * pre-compiled SQL statement that will produce <code>ResultSet</code>
	 * objects with the given type and concurrency
	 * @exception SQLException if a database access error occurs
	 *         or the given parameters are not <code>ResultSet</code>
	 *         constants indicating type and concurrency
	 * @since 1.2
	 *
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}
	
	/** Creates a <code>PreparedStatement</code> object that will generate
	 * <code>ResultSet</code> objects with the given type, concurrency,
	 * and holdability.
	 * <P>
	 * This method is the same as the <code>prepareStatement</code> method
	 * above, but it allows the default result set
	 * type, concurrency, and holdability to be overridden.
	 *
	 * @param sql a <code>String</code> object that is the SQL statement to
	 *            be sent to the database; may contain one or more ? IN
	 *            parameters
	 * @param resultSetType one of the following <code>ResultSet</code>
	 *        constants:
	 *         <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *         <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *         <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @param resultSetConcurrency one of the following <code>ResultSet</code>
	 *        constants:
	 *         <code>ResultSet.CONCUR_READ_ONLY</code> or
	 *         <code>ResultSet.CONCUR_UPDATABLE</code>
	 * @param resultSetHoldability one of the following <code>ResultSet</code>
	 *        constants:
	 *         <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
	 *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
	 * @return a new <code>PreparedStatement</code> object, containing the
	 *         pre-compiled SQL statement, that will generate
	 *         <code>ResultSet</code> objects with the given type,
	 *         concurrency, and holdability
	 * @exception SQLException if a database access error occurs
	 *            or the given parameters are not <code>ResultSet</code>
	 *            constants indicating type, concurrency, and holdability
	 * @see ResultSet
	 * @since 1.4
	 *
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return null;
	}
	
	/** Removes the given <code>Savepoint</code> object from the current
	 * transaction. Any reference to the savepoint after it have been removed
	 * will cause an <code>SQLException</code> to be thrown.
	 *
	 * @param savepoint the <code>Savepoint</code> object to be removed
	 * @exception SQLException if a database access error occurs or
	 *            the given <code>Savepoint</code> object is not a valid
	 *            savepoint in the current transaction
	 * @since 1.4
	 *
	 */
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
	}
	
	/** Undoes all changes made in the current transaction
	 * and releases any database locks currently held
	 * by this <code>Connection</code> object. This method should be
	 * used only when auto-commit mode has been disabled.
	 *
	 * @exception SQLException if a database access error occurs or this
	 *            <code>Connection</code> object is in auto-commit mode
	 * @see #setAutoCommit
	 *
	 */
	public void rollback() throws SQLException {
	}
	
	/** Undoes all changes made after the given <code>Savepoint</code> object
	 * was set.
	 * <P>
	 * This method should be used only when auto-commit has been disabled.
	 *
	 * @param savepoint the <code>Savepoint</code> object to roll back to
	 * @exception SQLException if a database access error occurs,
	 *            the <code>Savepoint</code> object is no longer valid,
	 *            or this <code>Connection</code> object is currently in
	 *            auto-commit mode
	 * @see Savepoint
	 * @see #rollback
	 * @since 1.4
	 *
	 */
	public void rollback(Savepoint savepoint) throws SQLException {
	}
	
	/** Sets this connection's auto-commit mode to the given state.
	 * If a connection is in auto-commit mode, then all its SQL
	 * statements will be executed and committed as individual
	 * transactions.  Otherwise, its SQL statements are grouped into
	 * transactions that are terminated by a call to either
	 * the method <code>commit</code> or the method <code>rollback</code>.
	 * By default, new connections are in auto-commit
	 * mode.
	 * <P>
	 * The commit occurs when the statement completes or the next
	 * execute occurs, whichever comes first. In the case of
	 * statements returning a <code>ResultSet</code> object,
	 * the statement completes when the last row of the
	 * <code>ResultSet</code> object has been retrieved or the
	 * <code>ResultSet</code> object has been closed. In advanced cases, a single
	 * statement may return multiple results as well as output
	 * parameter values. In these cases, the commit occurs when all results and
	 * output parameter values have been retrieved.
	 * <P>
	 * <B>NOTE:</B>  If this method is called during a transaction, the
	 * transaction is committed.
	 *
	 * @param autoCommit <code>true</code> to enable auto-commit mode;
	 *         <code>false</code> to disable it
	 * @exception SQLException if a database access error occurs
	 * @see #getAutoCommit
	 *
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
	}
	
	/** Sets the given catalog name in order to select
	 * a subspace of this <code>Connection</code> object's database
	 * in which to work.
	 * <P>
	 * If the driver does not support catalogs, it will
	 * silently ignore this request.
	 *
	 * @param catalog the name of a catalog (subspace in this
	 *        <code>Connection</code> object's database) in which to work
	 * @exception SQLException if a database access error occurs
	 * @see #getCatalog
	 *
	 */
	public void setCatalog(String catalog) throws SQLException {
	}
	
	/** Changes the holdability of <code>ResultSet</code> objects
	 * created using this <code>Connection</code> object to the given
	 * holdability.
	 *
	 * @param holdability a <code>ResultSet</code> holdability constant; one of
	 *        <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
	 *        <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
	 * @throws SQLException if a database access occurs, the given parameter
	 *         is not a <code>ResultSet</code> constant indicating holdability,
	 *         or the given holdability is not supported
	 * @see #getHoldability
	 * @see ResultSet
	 * @since 1.4
	 *
	 */
	public void setHoldability(int holdability) throws SQLException {
	}
	
	/** Puts this connection in read-only mode as a hint to the driver to enable
	 * database optimizations.
	 *
	 * <P><B>Note:</B> This method cannot be called during a transaction.
	 *
	 * @param readOnly <code>true</code> enables read-only mode;
	 *        <code>false</code> disables it
	 * @exception SQLException if a database access error occurs or this
	 *            method is called during a transaction
	 *
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
	}
	
	/** Creates an unnamed savepoint in the current transaction and
	 * returns the new <code>Savepoint</code> object that represents it.
	 *
	 * @return the new <code>Savepoint</code> object
	 * @exception SQLException if a database access error occurs
	 *            or this <code>Connection</code> object is currently in
	 *            auto-commit mode
	 * @see Savepoint
	 * @since 1.4
	 *
	 */
	public Savepoint setSavepoint() throws SQLException {
		return null;
	}
	
	/** Creates a savepoint with the given name in the current transaction
	 * and returns the new <code>Savepoint</code> object that represents it.
	 *
	 * @param name a <code>String</code> containing the name of the savepoint
	 * @return the new <code>Savepoint</code> object
	 * @exception SQLException if a database access error occurs
	 *            or this <code>Connection</code> object is currently in
	 *            auto-commit mode
	 * @see Savepoint
	 * @since 1.4
	 *
	 */
	public Savepoint setSavepoint(String name) throws SQLException {
		return null;
	}
	
	/** Attempts to change the transaction isolation level for this
	 * <code>Connection</code> object to the one given.
	 * The constants defined in the interface <code>Connection</code>
	 * are the possible transaction isolation levels.
	 * <P>
	 * <B>Note:</B> If this method is called during a transaction, the result
	 * is implementation-defined.
	 *
	 * @param level one of the following <code>Connection</code> constants:
	 *        <code>Connection.TRANSACTION_READ_UNCOMMITTED</code>,
	 *        <code>Connection.TRANSACTION_READ_COMMITTED</code>,
	 *        <code>Connection.TRANSACTION_REPEATABLE_READ</code>, or
	 *        <code>Connection.TRANSACTION_SERIALIZABLE</code>.
	 *        (Note that <code>Connection.TRANSACTION_NONE</code> cannot be used
	 *        because it specifies that transactions are not supported.)
	 * @exception SQLException if a database access error occurs
	 *            or the given parameter is not one of the <code>Connection</code>
	 *            constants
	 * @see DatabaseMetaData#supportsTransactionIsolationLevel
	 * @see #getTransactionIsolation
	 *
	 */
	public void setTransactionIsolation(int level) throws SQLException {
	}
	
	/** Installs the given <code>TypeMap</code> object as the type map for
	 * this <code>Connection</code> object.  The type map will be used for the
	 * custom mapping of SQL structured types and distinct types.
	 *
	 * @param map the <code>java.util.Map</code> object to install
	 *        as the replacement for this <code>Connection</code>
	 *        object's default type map
	 * @exception SQLException if a database access error occurs or
	 *        the given parameter is not a <code>java.util.Map</code>
	 *        object
	 * @since 1.2
	 * @see #getTypeMap
	 *
	 */
	public void setTypeMap(java.util.Map map) throws SQLException {
	}
}
