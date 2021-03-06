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
 * MetaData.java
 *
 * Created on January 31, 2003, 9:30 PM
 */

package com.rohanclan.ashpool.jdbc;

import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.rohanclan.ashpool.core.AResultSet;
import com.rohanclan.ashpool.core.Functions;
/**
 *
 * @author  rob 
 */
public class MetaData implements java.sql.DatabaseMetaData {
	
	/** handle to the connection */
	private Connection conn;
	  
	/** Creates a new instance of MetaData */
	public MetaData() {
		this(null);
	}
	
	public MetaData(Connection connhndl){
		this.conn = connhndl;
	} 
	
	/** Retrieves whether the current user can call all the procedures
	 * returned by the method <code>getProcedures</code>.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean allProceduresAreCallable() throws SQLException {
		return true;
	}
	
	/** Retrieves whether the current user can use all the tables returned
	 * by the method <code>getTables</code> in a <code>SELECT</code>
	 * statement.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean allTablesAreSelectable() throws SQLException {
		return true;
	}
	
	/** Retrieves whether a data definition statement within a transaction forces
	 * the transaction to commit.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database ignores a data definition statement
	 * within a transaction.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return true;
	}
	
	/** Retrieves whether or not a visible row delete can be detected by
	 * calling the method <code>ResultSet.rowDeleted</code>.  If the method
	 * <code>deletesAreDetected</code> returns <code>false</code>, it means that
	 * deleted rows are removed from the result set.
	 *
	 * @param type the <code>ResultSet</code> type; one of
	 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @return <code>true</code> if deletes are detected by the given result set type;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public boolean deletesAreDetected(int type) throws SQLException {
		return false;
	}
	
	/** Retrieves whether the return value for the method
	 * <code>getMaxRowSize</code> includes the SQL data types
	 * <code>LONGVARCHAR</code> and <code>LONGVARBINARY</code>.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return true;
	}
	 
	/** Retrieves a description of the given attribute of the given type
	 * for a user-defined type (UDT) that is available in the given schema
	 * and catalog.
	 * <P>
	 * Descriptions are returned only for attributes of UDTs matching the
	 * catalog, schema, type, and attribute name criteria. They are ordered by
	 * TYPE_SCHEM, TYPE_NAME and ORDINAL_POSITION. This description
	 * does not contain inherited attributes.
	 * <P>
	 * The <code>ResultSet</code> object that is returned has the following
	 * columns:
	 * <OL>
	 *  <LI><B>TYPE_CAT</B> String => type catalog (may be <code>null</code>)
	 * 	<LI><B>TYPE_SCHEM</B> String => type schema (may be <code>null</code>)
	 * 	<LI><B>TYPE_NAME</B> String => type name
	 * 	<LI><B>ATTR_NAME</B> String => attribute name
	 * 	<LI><B>DATA_TYPE</B> short => attribute type SQL type from java.sql.Types
	 * 	<LI><B>ATTR_TYPE_NAME</B> String => Data source dependent type name.
	 *  For a UDT, the type name is fully qualified. For a REF, the type name is
	 *  fully qualified and represents the target type of the reference type.
	 * 	<LI><B>ATTR_SIZE</B> int => column size.  For char or date
	 * 	    types this is the maximum number of characters; for numeric or
	 * 	    decimal types this is precision.
	 * 	<LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
	 * 	<LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
	 * 	<LI><B>NULLABLE</B> int => whether NULL is allowed
	 *      <UL>
	 *      <LI> attributeNoNulls - might not allow NULL values
	 *      <LI> attributeNullable - definitely allows NULL values
	 *      <LI> attributeNullableUnknown - nullability unknown
	 *      </UL>
	 * 	<LI><B>REMARKS</B> String => comment describing column (may be <code>null</code>)
	 * 	<LI><B>ATTR_DEF</B> String => default value (may be <code>null</code>)
	 * 	<LI><B>SQL_DATA_TYPE</B> int => unused
	 * 	<LI><B>SQL_DATETIME_SUB</B> int => unused
	 * 	<LI><B>CHAR_OCTET_LENGTH</B> int => for char types the
	 *       maximum number of bytes in the column
	 * 	<LI><B>ORDINAL_POSITION</B> int	=> index of column in table
	 *      (starting at 1)
	 * 	<LI><B>IS_NULLABLE</B> String => "NO" means column definitely
	 *      does not allow NULL values; "YES" means the column might
	 *      allow NULL values.  An empty string means unknown.
	 *  <LI><B>SCOPE_CATALOG</B> String => catalog of table that is the
	 *      scope of a reference attribute (<code>null</code> if DATA_TYPE isn't REF)
	 *  <LI><B>SCOPE_SCHEMA</B> String => schema of table that is the
	 *      scope of a reference attribute (<code>null</code> if DATA_TYPE isn't REF)
	 *  <LI><B>SCOPE_TABLE</B> String => table name that is the scope of a
	 *      reference attribute (<code>null</code> if the DATA_TYPE isn't REF)
	 * <LI><B>SOURCE_DATA_TYPE</B> short => source type of a distinct type or user-generated
	 *      Ref type,SQL type from java.sql.Types (<code>null</code> if DATA_TYPE
	 *      isn't DISTINCT or user-generated REF)
	 *  </OL>
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schemaPattern a schema name pattern; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param typeNamePattern a type name pattern; must match the
	 *        type name as it is stored in the database
	 * @param attributeNamePattern an attribute name pattern; must match the attribute
	 *        name as it is declared in the database
	 * @return a <code>ResultSet</code> object in which each row is an
	 *         attribute description
	 * @exception SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public java.sql.ResultSet getAttributes(String catalog, String schemaPattern, 
		String typeNamePattern, String attributeNamePattern) throws SQLException {
		
		return new com.rohanclan.ashpool.core.AResultSet();
	}
	
	/** Retrieves a description of a table's optimal set of columns that
	 * uniquely identifies a row. They are ordered by SCOPE.
	 *
	 * <P>Each column description has the following columns:
	 *  <OL>
	 * 	<LI><B>SCOPE</B> short => actual scope of result
	 *      <UL>
	 *      <LI> bestRowTemporary - very temporary, while using row
	 *      <LI> bestRowTransaction - valid for remainder of current transaction
	 *      <LI> bestRowSession - valid for remainder of current session
	 *      </UL>
	 * 	<LI><B>COLUMN_NAME</B> String => column name
	 * 	<LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
	 * 	<LI><B>TYPE_NAME</B> String => Data source dependent type name,
	 *  for a UDT the type name is fully qualified
	 * 	<LI><B>COLUMN_SIZE</B> int => precision
	 * 	<LI><B>BUFFER_LENGTH</B> int => not used
	 * 	<LI><B>DECIMAL_DIGITS</B> short	 => scale
	 * 	<LI><B>PSEUDO_COLUMN</B> short => is this a pseudo column
	 *      like an Oracle ROWID
	 *      <UL>
	 *      <LI> bestRowUnknown - may or may not be pseudo column
	 *      <LI> bestRowNotPseudo - is NOT a pseudo column
	 *      <LI> bestRowPseudo - is a pseudo column
	 *      </UL>
	 *  </OL>
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schema a schema name; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param table a table name; must match the table name as it is stored
	 *        in the database
	 * @param scope the scope of interest; use same values as SCOPE
	 * @param nullable include columns that are nullable.
	 * @return <code>ResultSet</code> - each row is a column description
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public java.sql.ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
		return null;
	}
	
	/** Retrieves the <code>String</code> that this database uses as the
	 * separator between a catalog and table name.
	 *
	 * @return the separator string
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getCatalogSeparator() throws SQLException {
		return "";
	}
	
	/** Retrieves the database vendor's preferred term for "catalog".
	 *
	 * @return the vendor term for "catalog"
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getCatalogTerm() throws SQLException {
		return "";
	}
	
	/** Retrieves the catalog names available in this database.  The results
	 * are ordered by catalog name.
	 *
	 * <P>The catalog column is:
	 *  <OL>
	 * 	<LI><B>TABLE_CAT</B> String => catalog name
	 *  </OL>
	 *
	 * @return a <code>ResultSet</code> object in which each row has a
	 *         single <code>String</code> column that is a catalog name
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public java.sql.ResultSet getCatalogs() throws SQLException {
		AResultSet ars = new AResultSet();
		ars.setQuickResultSet("TABLE_CAT","default");
		
		return ars;
	}
	
	/** Retrieves a description of the access rights for a table's columns.
	 *
	 * <P>Only privileges matching the column name criteria are
	 * returned.  They are ordered by COLUMN_NAME and PRIVILEGE.
	 *
	 * <P>Each privilige description has the following columns:
	 *  <OL>
	 * 	<LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
	 * 	<LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
	 * 	<LI><B>TABLE_NAME</B> String => table name
	 * 	<LI><B>COLUMN_NAME</B> String => column name
	 * 	<LI><B>GRANTOR</B> => grantor of access (may be <code>null</code>)
	 * 	<LI><B>GRANTEE</B> String => grantee of access
	 * 	<LI><B>PRIVILEGE</B> String => name of access (SELECT,
	 *      INSERT, UPDATE, REFRENCES, ...)
	 * 	<LI><B>IS_GRANTABLE</B> String => "YES" if grantee is permitted
	 *      to grant to others; "NO" if not; <code>null</code> if unknown
	 *  </OL>
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schema a schema name; must match the schema name as it is
	 *        stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param table a table name; must match the table name as it is
	 *        stored in the database
	 * @param columnNamePattern a column name pattern; must match the column
	 *        name as it is stored in the database
	 * @return <code>ResultSet</code> - each row is a column privilege description
	 * @exception SQLException if a database access error occurs
	 * @see #getSearchStringEscape
	 *
	 */
	public java.sql.ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
		return null;
	}
	
	/** Retrieves a description of table columns available in
	 * the specified catalog.
	 *
	 * <P>Only column descriptions matching the catalog, schema, table
	 * and column name criteria are returned.  They are ordered by
	 * <code>TABLE_SCHEM</code>, <code>TABLE_NAME</code>, and
	 * <code>ORDINAL_POSITION</code>.
	 *
	 * <P>Each column description has the following columns:
	 *  <OL>
	 * 	<LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
	 * 	<LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
	 * 	<LI><B>TABLE_NAME</B> String => table name
	 * 	<LI><B>COLUMN_NAME</B> String => column name
	 * 	<LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
	 * 	<LI><B>TYPE_NAME</B> String => Data source dependent type name,
	 *  for a UDT the type name is fully qualified
	 * 	<LI><B>COLUMN_SIZE</B> int => column size.  For char or date
	 * 	    types this is the maximum number of characters, for numeric or
	 * 	    decimal types this is precision.
	 * 	<LI><B>BUFFER_LENGTH</B> is not used.
	 * 	<LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
	 * 	<LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
	 * 	<LI><B>NULLABLE</B> int => is NULL allowed.
	 *      <UL>
	 *      <LI> columnNoNulls - might not allow <code>NULL</code> values
	 *      <LI> columnNullable - definitely allows <code>NULL</code> values
	 *      <LI> columnNullableUnknown - nullability unknown
	 *      </UL>
	 * 	<LI><B>REMARKS</B> String => comment describing column (may be <code>null</code>)
	 * 	<LI><B>COLUMN_DEF</B> String => default value (may be <code>null</code>)
	 * 	<LI><B>SQL_DATA_TYPE</B> int => unused
	 * 	<LI><B>SQL_DATETIME_SUB</B> int => unused
	 * 	<LI><B>CHAR_OCTET_LENGTH</B> int => for char types the
	 *       maximum number of bytes in the column
	 * 	<LI><B>ORDINAL_POSITION</B> int	=> index of column in table
	 *      (starting at 1)
	 * 	<LI><B>IS_NULLABLE</B> String => "NO" means column definitely
	 *      does not allow NULL values; "YES" means the column might
	 *      allow NULL values.  An empty string means nobody knows.
	 *  <LI><B>SCOPE_CATLOG</B> String => catalog of table that is the scope
	 *      of a reference attribute (<code>null</code> if DATA_TYPE isn't REF)
	 *  <LI><B>SCOPE_SCHEMA</B> String => schema of table that is the scope
	 *      of a reference attribute (<code>null</code> if the DATA_TYPE isn't REF)
	 *  <LI><B>SCOPE_TABLE</B> String => table name that this the scope
	 *      of a reference attribure (<code>null</code> if the DATA_TYPE isn't REF)
	 *  <LI><B>SOURCE_DATA_TYPE</B> short => source type of a distinct type or user-generated
	 *      Ref type, SQL type from java.sql.Types (<code>null</code> if DATA_TYPE
	 *      isn't DISTINCT or user-generated REF)
	 *  </OL>
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schemaPattern a schema name pattern; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param tableNamePattern a table name pattern; must match the
	 *        table name as it is stored in the database
	 * @param columnNamePattern a column name pattern; must match the column
	 *        name as it is stored in the database
	 * @return <code>ResultSet</code> - each row is a column description
	 * @exception SQLException if a database access error occurs
	 * @see #getSearchStringEscape
	 *
	 */
	public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
		try{
			return conn.getConnectionManager().executeStatement("select columns " + tableNamePattern);
		} catch(Exception e) {
			throw new SQLException("Can't get columns for " + tableNamePattern 	+ " because " + e.toString()
			);
		}
	}
	
	/** Retrieves the connection that produced this metadata object.
	 * <P>
	 * @return the connection that produced this metadata object
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public java.sql.Connection getConnection() throws SQLException {
		return conn;
	}
	
	/** Retrieves a description of the foreign key columns in the given foreign key
	 * table that reference the primary key columns of the given primary key
	 * table (describe how one table imports another's key). This
	 * should normally return a single foreign key/primary key pair because
	 * most tables import a foreign key from a table only once.  They
	 * are ordered by FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, and
	 * KEY_SEQ.
	 *
	 * <P>Each foreign key column description has the following columns:
	 *  <OL>
	 * 	<LI><B>PKTABLE_CAT</B> String => primary key table catalog (may be <code>null</code>)
	 * 	<LI><B>PKTABLE_SCHEM</B> String => primary key table schema (may be <code>null</code>)
	 * 	<LI><B>PKTABLE_NAME</B> String => primary key table name
	 * 	<LI><B>PKCOLUMN_NAME</B> String => primary key column name
	 * 	<LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be <code>null</code>)
	 *      being exported (may be <code>null</code>)
	 * 	<LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be <code>null</code>)
	 *      being exported (may be <code>null</code>)
	 * 	<LI><B>FKTABLE_NAME</B> String => foreign key table name
	 *      being exported
	 * 	<LI><B>FKCOLUMN_NAME</B> String => foreign key column name
	 *      being exported
	 * 	<LI><B>KEY_SEQ</B> short => sequence number within foreign key
	 * 	<LI><B>UPDATE_RULE</B> short => What happens to
	 *       foreign key when primary is updated:
	 *      <UL>
	 *      <LI> importedNoAction - do not allow update of primary
	 *               key if it has been imported
	 *      <LI> importedKeyCascade - change imported key to agree
	 *               with primary key update
	 *      <LI> importedKeySetNull - change imported key to <code>NULL</code> if
	 *               its primary key has been updated
	 *      <LI> importedKeySetDefault - change imported key to default values
	 *               if its primary key has been updated
	 *      <LI> importedKeyRestrict - same as importedKeyNoAction
	 *                                 (for ODBC 2.x compatibility)
	 *      </UL>
	 * 	<LI><B>DELETE_RULE</B> short => What happens to
	 *      the foreign key when primary is deleted.
	 *      <UL>
	 *      <LI> importedKeyNoAction - do not allow delete of primary
	 *               key if it has been imported
	 *      <LI> importedKeyCascade - delete rows that import a deleted key
	 *      <LI> importedKeySetNull - change imported key to <code>NULL</code> if
	 *               its primary key has been deleted
	 *      <LI> importedKeyRestrict - same as importedKeyNoAction
	 *                                 (for ODBC 2.x compatibility)
	 *      <LI> importedKeySetDefault - change imported key to default if
	 *               its primary key has been deleted
	 *      </UL>
	 * 	<LI><B>FK_NAME</B> String => foreign key name (may be <code>null</code>)
	 * 	<LI><B>PK_NAME</B> String => primary key name (may be <code>null</code>)
	 * 	<LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key
	 *      constraints be deferred until commit
	 *      <UL>
	 *      <LI> importedKeyInitiallyDeferred - see SQL92 for definition
	 *      <LI> importedKeyInitiallyImmediate - see SQL92 for definition
	 *      <LI> importedKeyNotDeferrable - see SQL92 for definition
	 *      </UL>
	 *  </OL>
	 *
	 * @param primaryCatalog a catalog name; must match the catalog name
	 * as it is stored in the database; "" retrieves those without a
	 * catalog; <code>null</code> means drop catalog name from the selection criteria
	 * @param primarySchema a schema name; must match the schema name as
	 * it is stored in the database; "" retrieves those without a schema;
	 * <code>null</code> means drop schema name from the selection criteria
	 * @param primaryTable the name of the table that exports the key; must match
	 * the table name as it is stored in the database
	 * @param foreignCatalog a catalog name; must match the catalog name as
	 * it is stored in the database; "" retrieves those without a
	 * catalog; <code>null</code> means drop catalog name from the selection criteria
	 * @param foreignSchema a schema name; must match the schema name as it
	 * is stored in the database; "" retrieves those without a schema;
	 * <code>null</code> means drop schema name from the selection criteria
	 * @param foreignTable the name of the table that imports the key; must match
	 * the table name as it is stored in the database
	 * @return <code>ResultSet</code> - each row is a foreign key column description
	 * @exception SQLException if a database access error occurs
	 * @see #getImportedKeys
	 *
	 */
	public java.sql.ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
		return null;
	}
	
	/** Retrieves the major version number of the underlying database.
	 *
	 * @return the underlying database's major version
	 * @exception SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public int getDatabaseMajorVersion() throws SQLException {
		return 0;
	}
	
	/** Retrieves the minor version number of the underlying database.
	 *
	 * @return underlying database's minor version
	 * @exception SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public int getDatabaseMinorVersion() throws SQLException {
		return 1;
	}
	
	/** Retrieves the name of this database product.
	 *
	 * @return database product name
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getDatabaseProductName() throws SQLException {
		return "Ashpool XML";
	}
	
	/** Retrieves the version number of this database product.
	 *
	 * @return database version number
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getDatabaseProductVersion() throws SQLException {
		return "1";
	}
	
	/** Retrieves this database's default transaction isolation level.  The
	 * possible values are defined in <code>java.sql.Connection</code>.
	 *
	 * @return the default isolation level
	 * @exception SQLException if a database access error occurs
	 * @see Connection
	 *
	 */
	public int getDefaultTransactionIsolation() throws SQLException {
		return 0;
	}
	
	/** Retrieves this JDBC driver's major version number.
	 *
	 * @return JDBC driver major version
	 *
	 */
	public int getDriverMajorVersion() {
		return 0;
	}
	
	/** Retrieves this JDBC driver's minor version number.
	 *
	 * @return JDBC driver minor version number
	 *
	 */
	public int getDriverMinorVersion() {
		return 1;
	}
	
	/** Retrieves the name of this JDBC driver.
	 *
	 * @return JDBC driver name
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getDriverName() throws SQLException {
		return "Ashpool JDBC Driver";
	}
	
	/** Retrieves the version number of this JDBC driver as a <code>String</code>.
	 *
	 * @return JDBC driver version
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getDriverVersion() throws SQLException {
		return "1";
	}
	
	/** Retrieves a description of the foreign key columns that reference the
	 * given table's primary key columns (the foreign keys exported by a
	 * table).  They are ordered by FKTABLE_CAT, FKTABLE_SCHEM,
	 * FKTABLE_NAME, and KEY_SEQ.
	 *
	 * <P>Each foreign key column description has the following columns:
	 *  <OL>
	 * 	<LI><B>PKTABLE_CAT</B> String => primary key table catalog (may be <code>null</code>)
	 * 	<LI><B>PKTABLE_SCHEM</B> String => primary key table schema (may be <code>null</code>)
	 * 	<LI><B>PKTABLE_NAME</B> String => primary key table name
	 * 	<LI><B>PKCOLUMN_NAME</B> String => primary key column name
	 * 	<LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be <code>null</code>)
	 *      being exported (may be <code>null</code>)
	 * 	<LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be <code>null</code>)
	 *      being exported (may be <code>null</code>)
	 * 	<LI><B>FKTABLE_NAME</B> String => foreign key table name
	 *      being exported
	 * 	<LI><B>FKCOLUMN_NAME</B> String => foreign key column name
	 *      being exported
	 * 	<LI><B>KEY_SEQ</B> short => sequence number within foreign key
	 * 	<LI><B>UPDATE_RULE</B> short => What happens to
	 *       foreign key when primary is updated:
	 *      <UL>
	 *      <LI> importedNoAction - do not allow update of primary
	 *               key if it has been imported
	 *      <LI> importedKeyCascade - change imported key to agree
	 *               with primary key update
	 *      <LI> importedKeySetNull - change imported key to <code>NULL</code> if
	 *               its primary key has been updated
	 *      <LI> importedKeySetDefault - change imported key to default values
	 *               if its primary key has been updated
	 *      <LI> importedKeyRestrict - same as importedKeyNoAction
	 *                                 (for ODBC 2.x compatibility)
	 *      </UL>
	 * 	<LI><B>DELETE_RULE</B> short => What happens to
	 *      the foreign key when primary is deleted.
	 *      <UL>
	 *      <LI> importedKeyNoAction - do not allow delete of primary
	 *               key if it has been imported
	 *      <LI> importedKeyCascade - delete rows that import a deleted key
	 *      <LI> importedKeySetNull - change imported key to <code>NULL</code> if
	 *               its primary key has been deleted
	 *      <LI> importedKeyRestrict - same as importedKeyNoAction
	 *                                 (for ODBC 2.x compatibility)
	 *      <LI> importedKeySetDefault - change imported key to default if
	 *               its primary key has been deleted
	 *      </UL>
	 * 	<LI><B>FK_NAME</B> String => foreign key name (may be <code>null</code>)
	 * 	<LI><B>PK_NAME</B> String => primary key name (may be <code>null</code>)
	 * 	<LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key
	 *      constraints be deferred until commit
	 *      <UL>
	 *      <LI> importedKeyInitiallyDeferred - see SQL92 for definition
	 *      <LI> importedKeyInitiallyImmediate - see SQL92 for definition
	 *      <LI> importedKeyNotDeferrable - see SQL92 for definition
	 *      </UL>
	 *  </OL>
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in this database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schema a schema name; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param table a table name; must match the table name as it is stored
	 *        in this database
	 * @return a <code>ResultSet</code> object in which each row is a
	 *         foreign key column description
	 * @exception SQLException if a database access error occurs
	 * @see #getImportedKeys
	 *
	 */
	public java.sql.ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
		return null;
	}
	
	/** Retrieves all the "extra" characters that can be used in unquoted
	 * identifier names (those beyond a-z, A-Z, 0-9 and _).
	 *
	 * @return the string containing the extra characters
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getExtraNameCharacters() throws SQLException {
		return "";
	}
	
	/** Retrieves the string used to quote SQL identifiers.
	 * This method returns a space " " if identifier quoting is not supported.
	 *
	 * @return the quoting string or a space if quoting is not supported
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getIdentifierQuoteString() throws SQLException {
		return " ";
	}
	
	/** Retrieves a description of the primary key columns that are
	 * referenced by a table's foreign key columns (the primary keys
	 * imported by a table).  They are ordered by PKTABLE_CAT,
	 * PKTABLE_SCHEM, PKTABLE_NAME, and KEY_SEQ.
	 *
	 * <P>Each primary key column description has the following columns:
	 *  <OL>
	 * 	<LI><B>PKTABLE_CAT</B> String => primary key table catalog
	 *      being imported (may be <code>null</code>)
	 * 	<LI><B>PKTABLE_SCHEM</B> String => primary key table schema
	 *      being imported (may be <code>null</code>)
	 * 	<LI><B>PKTABLE_NAME</B> String => primary key table name
	 *      being imported
	 * 	<LI><B>PKCOLUMN_NAME</B> String => primary key column name
	 *      being imported
	 * 	<LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be <code>null</code>)
	 * 	<LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be <code>null</code>)
	 * 	<LI><B>FKTABLE_NAME</B> String => foreign key table name
	 * 	<LI><B>FKCOLUMN_NAME</B> String => foreign key column name
	 * 	<LI><B>KEY_SEQ</B> short => sequence number within a foreign key
	 * 	<LI><B>UPDATE_RULE</B> short => What happens to a
	 *       foreign key when the primary key is updated:
	 *      <UL>
	 *      <LI> importedNoAction - do not allow update of primary
	 *               key if it has been imported
	 *      <LI> importedKeyCascade - change imported key to agree
	 *               with primary key update
	 *      <LI> importedKeySetNull - change imported key to <code>NULL</code>
	 *               if its primary key has been updated
	 *      <LI> importedKeySetDefault - change imported key to default values
	 *               if its primary key has been updated
	 *      <LI> importedKeyRestrict - same as importedKeyNoAction
	 *                                 (for ODBC 2.x compatibility)
	 *      </UL>
	 * 	<LI><B>DELETE_RULE</B> short => What happens to
	 *      the foreign key when primary is deleted.
	 *      <UL>
	 *      <LI> importedKeyNoAction - do not allow delete of primary
	 *               key if it has been imported
	 *      <LI> importedKeyCascade - delete rows that import a deleted key
	 *      <LI> importedKeySetNull - change imported key to NULL if
	 *               its primary key has been deleted
	 *      <LI> importedKeyRestrict - same as importedKeyNoAction
	 *                                 (for ODBC 2.x compatibility)
	 *      <LI> importedKeySetDefault - change imported key to default if
	 *               its primary key has been deleted
	 *      </UL>
	 * 	<LI><B>FK_NAME</B> String => foreign key name (may be <code>null</code>)
	 * 	<LI><B>PK_NAME</B> String => primary key name (may be <code>null</code>)
	 * 	<LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key
	 *      constraints be deferred until commit
	 *      <UL>
	 *      <LI> importedKeyInitiallyDeferred - see SQL92 for definition
	 *      <LI> importedKeyInitiallyImmediate - see SQL92 for definition
	 *      <LI> importedKeyNotDeferrable - see SQL92 for definition
	 *      </UL>
	 *  </OL>
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schema a schema name; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param table a table name; must match the table name as it is stored
	 *        in the database
	 * @return <code>ResultSet</code> - each row is a primary key column description
	 * @exception SQLException if a database access error occurs
	 * @see #getExportedKeys
	 *
	 */
	public java.sql.ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
		return null;
	}
	
	/** Retrieves a description of the given table's indices and statistics. They are
	 * ordered by NON_UNIQUE, TYPE, INDEX_NAME, and ORDINAL_POSITION.
	 *
	 * <P>Each index column description has the following columns:
	 *  <OL>
	 * 	<LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
	 * 	<LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
	 * 	<LI><B>TABLE_NAME</B> String => table name
	 * 	<LI><B>NON_UNIQUE</B> boolean => Can index values be non-unique.
	 *      false when TYPE is tableIndexStatistic
	 * 	<LI><B>INDEX_QUALIFIER</B> String => index catalog (may be <code>null</code>);
	 *      <code>null</code> when TYPE is tableIndexStatistic
	 * 	<LI><B>INDEX_NAME</B> String => index name; <code>null</code> when TYPE is
	 *      tableIndexStatistic
	 * 	<LI><B>TYPE</B> short => index type:
	 *      <UL>
	 *      <LI> tableIndexStatistic - this identifies table statistics that are
	 *           returned in conjuction with a table's index descriptions
	 *      <LI> tableIndexClustered - this is a clustered index
	 *      <LI> tableIndexHashed - this is a hashed index
	 *      <LI> tableIndexOther - this is some other style of index
	 *      </UL>
	 * 	<LI><B>ORDINAL_POSITION</B> short => column sequence number
	 *      within index; zero when TYPE is tableIndexStatistic
	 * 	<LI><B>COLUMN_NAME</B> String => column name; <code>null</code> when TYPE is
	 *      tableIndexStatistic
	 * 	<LI><B>ASC_OR_DESC</B> String => column sort sequence, "A" => ascending,
	 *      "D" => descending, may be <code>null</code> if sort sequence is not supported;
	 *      <code>null</code> when TYPE is tableIndexStatistic
	 * 	<LI><B>CARDINALITY</B> int => When TYPE is tableIndexStatistic, then
	 *      this is the number of rows in the table; otherwise, it is the
	 *      number of unique values in the index.
	 * 	<LI><B>PAGES</B> int => When TYPE is  tableIndexStatisic then
	 *      this is the number of pages used for the table, otherwise it
	 *      is the number of pages used for the current index.
	 * 	<LI><B>FILTER_CONDITION</B> String => Filter condition, if any.
	 *      (may be <code>null</code>)
	 *  </OL>
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in this database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schema a schema name; must match the schema name
	 *        as it is stored in this database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param table a table name; must match the table name as it is stored
	 *        in this database
	 * @param unique when true, return only indices for unique values;
	 *     when false, return indices regardless of whether unique or not
	 * @param approximate when true, result is allowed to reflect approximate
	 *     or out of data values; when false, results are requested to be
	 *     accurate
	 * @return <code>ResultSet</code> - each row is an index column description
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public java.sql.ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
		return null;
	}
	
	/** Retrieves the major JDBC version number for this
	 * driver.
	 *
	 * @return JDBC version major number
	 * @exception SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public int getJDBCMajorVersion() throws SQLException {
		return 0;
	}
	
	/** Retrieves the minor JDBC version number for this
	 * driver.
	 *
	 * @return JDBC version minor number
	 * @exception SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public int getJDBCMinorVersion() throws SQLException {
		return 1;
	}
	
	/** Retrieves the maximum number of hex characters this database allows in an
	 * inline binary literal.
	 *
	 * @return max the maximum length (in hex characters) for a binary literal;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxBinaryLiteralLength() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of characters that this database allows in a
	 * catalog name.
	 *
	 * @return the maximum number of characters allowed in a catalog name;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxCatalogNameLength() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of characters this database allows
	 * for a character literal.
	 *
	 * @return the maximum number of characters allowed for a character literal;
	 *      a result of zero means that there is no limit or the limit is
	 *      not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxCharLiteralLength() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of characters this database allows
	 * for a column name.
	 *
	 * @return the maximum number of characters allowed for a column name;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxColumnNameLength() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of columns this database allows in a
	 * <code>GROUP BY</code> clause.
	 *
	 * @return the maximum number of columns allowed;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxColumnsInGroupBy() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of columns this database allows in an index.
	 *
	 * @return the maximum number of columns allowed;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxColumnsInIndex() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of columns this database allows in an
	 * <code>ORDER BY</code> clause.
	 *
	 * @return the maximum number of columns allowed;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxColumnsInOrderBy() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of columns this database allows in a
	 * <code>SELECT</code> list.
	 *
	 * @return the maximum number of columns allowed;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxColumnsInSelect() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of columns this database allows in a table.
	 *
	 * @return the maximum number of columns allowed;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxColumnsInTable() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of concurrent connections to this
	 * database that are possible.
	 *
	 * @return the maximum number of active connections possible at one time;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxConnections() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of characters that this database allows in a
	 * cursor name.
	 *
	 * @return the maximum number of characters allowed in a cursor name;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxCursorNameLength() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of bytes this database allows for an
	 * index, including all of the parts of the index.
	 *
	 * @return the maximum number of bytes allowed; this limit includes the
	 *      composite of all the constituent parts of the index;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxIndexLength() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of characters that this database allows in a
	 * procedure name.
	 *
	 * @return the maximum number of characters allowed in a procedure name;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxProcedureNameLength() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of bytes this database allows in
	 * a single row.
	 *
	 * @return the maximum number of bytes allowed for a row; a result of
	 *         zero means that there is no limit or the limit is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxRowSize() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of characters that this database allows in a
	 * schema name.
	 *
	 * @return the maximum number of characters allowed in a schema name;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxSchemaNameLength() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of characters this database allows in
	 * an SQL statement.
	 *
	 * @return the maximum number of characters allowed for an SQL statement;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxStatementLength() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of active statements to this database
	 * that can be open at the same time.
	 *
	 * @return the maximum number of statements that can be open at one time;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxStatements() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of characters this database allows in
	 * a table name.
	 *
	 * @return the maximum number of characters allowed for a table name;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxTableNameLength() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of tables this database allows in a
	 * <code>SELECT</code> statement.
	 *
	 * @return the maximum number of tables allowed in a <code>SELECT</code>
	 *         statement; a result of zero means that there is no limit or
	 *         the limit is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxTablesInSelect() throws SQLException {
		return 0;
	}
	
	/** Retrieves the maximum number of characters this database allows in
	 * a user name.
	 *
	 * @return the maximum number of characters allowed for a user name;
	 *      a result of zero means that there is no limit or the limit
	 *      is not known
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getMaxUserNameLength() throws SQLException {
		return 0;
	}
	
	/** Retrieves a comma-separated list of math functions available with
	 * this database.  These are the Open /Open CLI math function names used in
	 * the JDBC function escape clause.
	 *
	 * @return the list of math functions supported by this database
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getNumericFunctions() throws SQLException {
		return conn.getConnectionManager().getNumericFunctions();
	}
	
	/** Retrieves a description of the given table's primary key columns.  They
	 * are ordered by COLUMN_NAME.
	 *
	 * <P>Each primary key column description has the following columns:
	 *  <OL>
	 * 	<LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
	 * 	<LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
	 * 	<LI><B>TABLE_NAME</B> String => table name
	 * 	<LI><B>COLUMN_NAME</B> String => column name
	 * 	<LI><B>KEY_SEQ</B> short => sequence number within primary key
	 * 	<LI><B>PK_NAME</B> String => primary key name (may be <code>null</code>)
	 *  </OL>
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schema a schema name; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param table a table name; must match the table name as it is stored
	 *        in the database
	 * @return <code>ResultSet</code> - each row is a primary key column description
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public java.sql.ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
		return null;
	}
	
	/** Retrieves a description of the given catalog's stored procedure parameter
	 * and result columns.
	 *
	 * <P>Only descriptions matching the schema, procedure and
	 * parameter name criteria are returned.  They are ordered by
	 * PROCEDURE_SCHEM and PROCEDURE_NAME. Within this, the return value,
	 * if any, is first. Next are the parameter descriptions in call
	 * order. The column descriptions follow in column number order.
	 *
	 * <P>Each row in the <code>ResultSet</code> is a parameter description or
	 * column description with the following fields:
	 *  <OL>
	 * 	<LI><B>PROCEDURE_CAT</B> String => procedure catalog (may be <code>null</code>)
	 * 	<LI><B>PROCEDURE_SCHEM</B> String => procedure schema (may be <code>null</code>)
	 * 	<LI><B>PROCEDURE_NAME</B> String => procedure name
	 * 	<LI><B>COLUMN_NAME</B> String => column/parameter name
	 * 	<LI><B>COLUMN_TYPE</B> Short => kind of column/parameter:
	 *      <UL>
	 *      <LI> procedureColumnUnknown - nobody knows
	 *      <LI> procedureColumnIn - IN parameter
	 *      <LI> procedureColumnInOut - INOUT parameter
	 *      <LI> procedureColumnOut - OUT parameter
	 *      <LI> procedureColumnReturn - procedure return value
	 *      <LI> procedureColumnResult - result column in <code>ResultSet</code>
	 *      </UL>
	 *  <LI><B>DATA_TYPE</B> short => SQL type from java.sql.Types
	 * 	<LI><B>TYPE_NAME</B> String => SQL type name, for a UDT type the
	 *  type name is fully qualified
	 * 	<LI><B>PRECISION</B> int => precision
	 * 	<LI><B>LENGTH</B> int => length in bytes of data
	 * 	<LI><B>SCALE</B> short => scale
	 * 	<LI><B>RADIX</B> short => radix
	 * 	<LI><B>NULLABLE</B> short => can it contain NULL.
	 *      <UL>
	 *      <LI> procedureNoNulls - does not allow NULL values
	 *      <LI> procedureNullable - allows NULL values
	 *      <LI> procedureNullableUnknown - nullability unknown
	 *      </UL>
	 * 	<LI><B>REMARKS</B> String => comment describing parameter/column
	 *  </OL>
	 *
	 * <P><B>Note:</B> Some databases may not return the column
	 * descriptions for a procedure. Additional columns beyond
	 * REMARKS can be defined by the database.
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schemaPattern a schema name pattern; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param procedureNamePattern a procedure name pattern; must match the
	 *        procedure name as it is stored in the database
	 * @param columnNamePattern a column name pattern; must match the column name
	 *        as it is stored in the database
	 * @return <code>ResultSet</code> - each row describes a stored procedure parameter or
	 *      column
	 * @exception SQLException if a database access error occurs
	 * @see #getSearchStringEscape
	 *
	 */
	public java.sql.ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
		return null;
	}
	
	/** Retrieves the database vendor's preferred term for "procedure".
	 *
	 * @return the vendor term for "procedure"
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getProcedureTerm() throws SQLException {
		return "";
	}
	
	/** Retrieves a description of the stored procedures available in the given
	 * catalog.
	 * <P>
	 * Only procedure descriptions matching the schema and
	 * procedure name criteria are returned.  They are ordered by
	 * <code>PROCEDURE_SCHEM</code> and <code>PROCEDURE_NAME</code>.
	 *
	 * <P>Each procedure description has the the following columns:
	 *  <OL>
	 * 	<LI><B>PROCEDURE_CAT</B> String => procedure catalog (may be <code>null</code>)
	 * 	<LI><B>PROCEDURE_SCHEM</B> String => procedure schema (may be <code>null</code>)
	 * 	<LI><B>PROCEDURE_NAME</B> String => procedure name
	 *  <LI> reserved for future use
	 *  <LI> reserved for future use
	 *  <LI> reserved for future use
	 * 	<LI><B>REMARKS</B> String => explanatory comment on the procedure
	 * 	<LI><B>PROCEDURE_TYPE</B> short => kind of procedure:
	 *      <UL>
	 *      <LI> procedureResultUnknown - May return a result
	 *      <LI> procedureNoResult - Does not return a result
	 *      <LI> procedureReturnsResult - Returns a result
	 *      </UL>
	 *  </OL>
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schemaPattern a schema name pattern; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param procedureNamePattern a procedure name pattern; must match the
	 *        procedure name as it is stored in the database
	 * @return <code>ResultSet</code> - each row is a procedure description
	 * @exception SQLException if a database access error occurs
	 * @see #getSearchStringEscape
	 *
	 */
	public java.sql.ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
		return conn.getConnectionManager().getProcedures();
	}
	
	/** Retrieves the default holdability of this <code>ResultSet</code>
	 * object.
	 *
	 * @return the default holdability; either
	 *         <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
	 *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
	 * @exception SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public int getResultSetHoldability() throws SQLException {
		return 0;
	}
	
	/** Retrieves a comma-separated list of all of this database's SQL keywords
	 * that are NOT also SQL92 keywords.
	 *
	 * @return the list of this database's keywords that are not also
	 *         SQL92 keywords
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getSQLKeywords() throws SQLException {
		return Functions.getAshpoolKeywords();
	}
	
	/** Indicates whether the SQLSTATEs returned by <code>SQLException.getSQLState</code>
	 * is X/Open (now known as Open Group) SQL CLI or SQL99.
	 * @return the type of SQLSTATEs, one of:
	 *        sqlStateXOpen or
	 *        sqlStateSQL99
	 * @throws SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public int getSQLStateType() throws SQLException {
		return 0;
	}
	
	/** Retrieves the database vendor's preferred term for "schema".
	 *
	 * @return the vendor term for "schema"
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getSchemaTerm() throws SQLException {
		return "xsd";
	}
	
	/** Retrieves the schema names available in this database.  The results
	 * are ordered by schema name.
	 *
	 * <P>The schema column is:
	 *  <OL>
	 * 	<LI><B>TABLE_SCHEM</B> String => schema name
	 *  <LI><B>TABLE_CATALOG</B> String => catalog name (may be <code>null</code>)
	 *  </OL>
	 *
	 * @return a <code>ResultSet</code> object in which each row is a
	 *         schema decription
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public java.sql.ResultSet getSchemas() throws SQLException {
		 //return conn.getConnectionManager().getTableManager().getTables();
		return conn.getConnectionManager().getTables();
	}
	
	/** Retrieves the string that can be used to escape wildcard characters.
	 * This is the string that can be used to escape '_' or '%' in
	 * the catalog search parameters that are a pattern (and therefore use one
	 * of the wildcard characters).
	 *
	 * <P>The '_' character represents any single character;
	 * the '%' character represents any sequence of zero or
	 * more characters.
	 *
	 * @return the string used to escape wildcard characters
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getSearchStringEscape() throws SQLException {
		return "";
	}
	
	/** Retrieves a comma-separated list of string functions available with
	 * this database.  These are the  Open Group CLI string function names used
	 * in the JDBC function escape clause.
	 *
	 * @return the list of string functions supported by this database
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getStringFunctions() throws SQLException {
		return conn.getConnectionManager().getStringFunctions();
	}
	
	/** Retrieves a description of the table hierarchies defined in a particular
	 * schema in this database.
	 *
	 * <P>Only supertable information for tables matching the catalog, schema
	 * and table name are returned. The table name parameter may be a fully-
	 * qualified name, in which case, the catalog and schemaPattern parameters
	 * are ignored. If a table does not have a super table, it is not listed here.
	 * Supertables have to be defined in the same catalog and schema as the
	 * sub tables. Therefore, the type description does not need to include
	 * this information for the supertable.
	 *
	 * <P>Each type description has the following columns:
	 *  <OL>
	 *  <LI><B>TABLE_CAT</B> String => the type's catalog (may be <code>null</code>)
	 *  <LI><B>TABLE_SCHEM</B> String => type's schema (may be <code>null</code>)
	 *  <LI><B>TABLE_NAME</B> String => type name
	 *  <LI><B>SUPERTABLE_NAME</B> String => the direct super type's name
	 *  </OL>
	 *
	 * <P><B>Note:</B> If the driver does not support type hierarchies, an
	 * empty result set is returned.
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog;
	 *        <code>null</code> means drop catalog name from the selection criteria
	 * @param schemaPattern a schema name pattern; "" retrieves those
	 *        without a schema
	 * @param tableNamePattern a table name pattern; may be a fully-qualified
	 *        name
	 * @return a <code>ResultSet</code> object in which each row is a type description
	 * @throws SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public java.sql.ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		return null;
	}
	
	/** Retrieves a description of the user-defined type (UDT) hierarchies defined in a
	 * particular schema in this database. Only the immediate super type/
	 * sub type relationship is modeled.
	 * <P>
	 * Only supertype information for UDTs matching the catalog,
	 * schema, and type name is returned. The type name parameter
	 * may be a fully-qualified name. When the UDT name supplied is a
	 * fully-qualified name, the catalog and schemaPattern parameters are
	 * ignored.
	 * <P>
	 * If a UDT does not have a direct super type, it is not listed here.
	 * A row of the <code>ResultSet</code> object returned by this method
	 * describes the designated UDT and a direct supertype. A row has the following
	 * columns:
	 *  <OL>
	 *  <LI><B>TYPE_CAT</B> String => the UDT's catalog (may be <code>null</code>)
	 *  <LI><B>TYPE_SCHEM</B> String => UDT's schema (may be <code>null</code>)
	 *  <LI><B>TYPE_NAME</B> String => type name of the UDT
	 *  <LI><B>SUPERTYPE_CAT</B> String => the direct super type's catalog
	 *                           (may be <code>null</code>)
	 *  <LI><B>SUPERTYPE_SCHEM</B> String => the direct super type's schema
	 *                             (may be <code>null</code>)
	 *  <LI><B>SUPERTYPE_NAME</B> String => the direct super type's name
	 *  </OL>
	 *
	 * <P><B>Note:</B> If the driver does not support type hierarchies, an
	 * empty result set is returned.
	 *
	 * @param catalog a catalog name; "" retrieves those without a catalog;
	 *        <code>null</code> means drop catalog name from the selection criteria
	 * @param schemaPattern a schema name pattern; "" retrieves those
	 *        without a schema
	 * @param typeNamePattern a UDT name pattern; may be a fully-qualified
	 *        name
	 * @return a <code>ResultSet</code> object in which a row gives information
	 *         about the designated UDT
	 * @throws SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public java.sql.ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
		return null;
	}
	
	/** Retrieves a comma-separated list of system functions available with
	 * this database.  These are the  Open Group CLI system function names used
	 * in the JDBC funsction escape clause.
	 *
	 * @return a list of system functions supported by this database
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getSystemFunctions() throws SQLException {
		return "";
	}
	
	/** Retrieves a description of the access rights for each table available
	 * in a catalog. Note that a table privilege applies to one or
	 * more columns in the table. It would be wrong to assume that
	 * this privilege applies to all columns (this may be true for
	 * some systems but is not true for all.)
	 *
	 * <P>Only privileges matching the schema and table name
	 * criteria are returned.  They are ordered by TABLE_SCHEM,
	 * TABLE_NAME, and PRIVILEGE.
	 *
	 * <P>Each privilige description has the following columns:
	 *  <OL>
	 * 	<LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
	 * 	<LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
	 * 	<LI><B>TABLE_NAME</B> String => table name
	 * 	<LI><B>GRANTOR</B> => grantor of access (may be <code>null</code>)
	 * 	<LI><B>GRANTEE</B> String => grantee of access
	 * 	<LI><B>PRIVILEGE</B> String => name of access (SELECT,
	 *      INSERT, UPDATE, REFRENCES, ...)
	 * 	<LI><B>IS_GRANTABLE</B> String => "YES" if grantee is permitted
	 *      to grant to others; "NO" if not; <code>null</code> if unknown
	 *  </OL>
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schemaPattern a schema name pattern; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param tableNamePattern a table name pattern; must match the
	 *        table name as it is stored in the database
	 * @return <code>ResultSet</code> - each row is a table privilege description
	 * @exception SQLException if a database access error occurs
	 * @see #getSearchStringEscape
	 *
	 */
	public java.sql.ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		return null;
	}
	
	/** Retrieves the table types available in this database.  The results
	 * are ordered by table type.
	 *
	 * <P>The table type is:
	 *  <OL>
	 * 	<LI><B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
	 * 			"VIEW",	"SYSTEM TABLE", "GLOBAL TEMPORARY",
	 * 			"LOCAL TEMPORARY", "ALIAS", "SYNONYM".
	 *  </OL>
	 *
	 * @return a <code>ResultSet</code> object in which each row has a
	 *         single <code>String</code> column that is a table type
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public ResultSet getTableTypes() throws SQLException {
		AResultSet rs = new AResultSet();
		List<Object> vTable  = new ArrayList<Object>();
		
		vTable.add("TABLE");
		vTable.add("LOCAL TEMPORARY");
		vTable.add("SYSTEM TABLE");
		//vTable.add("LOCAL TEMPORARY");
		rs.addColumn("TABLE_TYPE", vTable, Types.VARCHAR);
		return rs;
	}
	
	/** Retrieves a description of the tables available in the given catalog.
	 * Only table descriptions matching the catalog, schema, table
	 * name and type criteria are returned.  They are ordered by
	 * TABLE_TYPE, TABLE_SCHEM and TABLE_NAME.
	 * <P>
	 * Each table description has the following columns:
	 *  <OL>
	 * 	<LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
	 * 	<LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
	 * 	<LI><B>TABLE_NAME</B> String => table name
	 * 	<LI><B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
	 * 			"VIEW",	"SYSTEM TABLE", "GLOBAL TEMPORARY",
	 * 			"LOCAL TEMPORARY", "ALIAS", "SYNONYM".
	 * 	<LI><B>REMARKS</B> String => explanatory comment on the table
	 *  <LI><B>TYPE_CAT</B> String => the types catalog (may be <code>null</code>)
	 *  <LI><B>TYPE_SCHEM</B> String => the types schema (may be <code>null</code>)
	 *  <LI><B>TYPE_NAME</B> String => type name (may be <code>null</code>)
	 *  <LI><B>SELF_REFERENCING_COL_NAME</B> String => name of the designated
	 *                  "identifier" column of a typed table (may be <code>null</code>)
	 * 	<LI><B>REF_GENERATION</B> String => specifies how values in
	 *                  SELF_REFERENCING_COL_NAME are created. Values are
	 *                  "SYSTEM", "USER", "DERIVED". (may be <code>null</code>)
	 *  </OL>
	 *
	 * <P><B>Note:</B> Some databases may not return information for
	 * all tables.
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schemaPattern a schema name pattern; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param tableNamePattern a table name pattern; must match the
	 *        table name as it is stored in the database
	 * @param types a list of table types to include; <code>null</code> returns all types
	 * @return <code>ResultSet</code> - each row is a table description
	 * @exception SQLException if a database access error occurs
	 * @see #getSearchStringEscape
	 *
	 */
	public java.sql.ResultSet getTables(String catalog, String schemaPattern, 
				String tableNamePattern, String[] types) throws SQLException {
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
		
		return conn.getConnectionManager().getTables(types);
	}
	
	/** Retrieves a comma-separated list of the time and date functions available
	 * with this database.
	 *
	 * @return the list of time and date functions supported by this database
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getTimeDateFunctions() throws SQLException {
		return conn.getConnectionManager().getDateTimeFunctions();
	}
	
	/** Retrieves a description of all the standard SQL types supported by
	 * this database. They are ordered by DATA_TYPE and then by how
	 * closely the data type maps to the corresponding JDBC SQL type.
	 *
	 * <P>Each type description has the following columns:
	 *  <OL>
	 * 	<LI><B>TYPE_NAME</B> String => Type name
	 * 	<LI><B>DATA_TYPE</B> short => SQL data type from java.sql.Types
	 * 	<LI><B>PRECISION</B> int => maximum precision
	 * 	<LI><B>LITERAL_PREFIX</B> String => prefix used to quote a literal
	 *      (may be <code>null</code>)
	 * 	<LI><B>LITERAL_SUFFIX</B> String => suffix used to quote a literal
	 *      (may be <code>null</code>)
	 * 	<LI><B>CREATE_PARAMS</B> String => parameters used in creating
	 *      the type (may be <code>null</code>)
	 * 	<LI><B>NULLABLE</B> short => can you use NULL for this type.
	 *      <UL>
	 *      <LI> typeNoNulls - does not allow NULL values
	 *      <LI> typeNullable - allows NULL values
	 *      <LI> typeNullableUnknown - nullability unknown
	 *      </UL>
	 * 	<LI><B>CASE_SENSITIVE</B> boolean=> is it case sensitive.
	 * 	<LI><B>SEARCHABLE</B> short => can you use "WHERE" based on this type:
	 *      <UL>
	 *      <LI> typePredNone - No support
	 *      <LI> typePredChar - Only supported with WHERE .. LIKE
	 *      <LI> typePredBasic - Supported except for WHERE .. LIKE
	 *      <LI> typeSearchable - Supported for all WHERE ..
	 *      </UL>
	 * 	<LI><B>UNSIGNED_ATTRIBUTE</B> boolean => is it unsigned.
	 * 	<LI><B>FIXED_PREC_SCALE</B> boolean => can it be a money value.
	 * 	<LI><B>AUTO_INCREMENT</B> boolean => can it be used for an
	 *      auto-increment value.
	 * 	<LI><B>LOCAL_TYPE_NAME</B> String => localized version of type name
	 *      (may be <code>null</code>)
	 * 	<LI><B>MINIMUM_SCALE</B> short => minimum scale supported
	 * 	<LI><B>MAXIMUM_SCALE</B> short => maximum scale supported
	 * 	<LI><B>SQL_DATA_TYPE</B> int => unused
	 * 	<LI><B>SQL_DATETIME_SUB</B> int => unused
	 * 	<LI><B>NUM_PREC_RADIX</B> int => usually 2 or 10
	 *  </OL>
	 *
	 * @return a <code>ResultSet</code> object in which each row is an SQL
	 *         type description
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public java.sql.ResultSet getTypeInfo() throws SQLException {
		try{
			return conn.getConnectionManager().executeStatement("select types");
		}catch(Exception e){
			throw new SQLException(e.toString());
		}
	}
	
	/** Retrieves a description of the user-defined types (UDTs) defined
	 * in a particular schema.  Schema-specific UDTs may have type
	 * <code>JAVA_OBJECT</code>, <code>STRUCT</code>,
	 * or <code>DISTINCT</code>.
	 *
	 * <P>Only types matching the catalog, schema, type name and type
	 * criteria are returned.  They are ordered by DATA_TYPE, TYPE_SCHEM
	 * and TYPE_NAME.  The type name parameter may be a fully-qualified
	 * name.  In this case, the catalog and schemaPattern parameters are
	 * ignored.
	 *
	 * <P>Each type description has the following columns:
	 *  <OL>
	 * 	<LI><B>TYPE_CAT</B> String => the type's catalog (may be <code>null</code>)
	 * 	<LI><B>TYPE_SCHEM</B> String => type's schema (may be <code>null</code>)
	 * 	<LI><B>TYPE_NAME</B> String => type name
	 *  <LI><B>CLASS_NAME</B> String => Java class name
	 * 	<LI><B>DATA_TYPE</B> String => type value defined in java.sql.Types.
	 *     One of JAVA_OBJECT, STRUCT, or DISTINCT
	 * 	<LI><B>REMARKS</B> String => explanatory comment on the type
	 *  <LI><B>BASE_TYPE</B> short => type code of the source type of a
	 *     DISTINCT type or the type that implements the user-generated
	 *     reference type of the SELF_REFERENCING_COLUMN of a structured
	 *     type as defined in java.sql.Types (<code>null</code> if DATA_TYPE is not
	 *     DISTINCT or not STRUCT with REFERENCE_GENERATION = USER_DEFINED)
	 *  </OL>
	 *
	 * <P><B>Note:</B> If the driver does not support UDTs, an empty
	 * result set is returned.
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schemaPattern a schema pattern name; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param typeNamePattern a type name pattern; must match the type name
	 *        as it is stored in the database; may be a fully qualified name
	 * @param types a list of user-defined types (JAVA_OBJECT,
	 *        STRUCT, or DISTINCT) to include; <code>null</code> returns all types
	 * @return <code>ResultSet</code> object in which each row describes a UDT
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public java.sql.ResultSet getUDTs(String catalog, String schemaPattern, 
		String typeNamePattern, int[] types) throws SQLException {
		
		return new AResultSet();
	}
	
	/** Retrieves the URL for this DBMS.
	 *
	 * @return the URL for this DBMS or <code>null</code> if it cannot be
	 *          generated
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getURL() throws SQLException {
		return conn.getConnectionManager().getTableManager().getDatastoreURI();
	}
	
	/** Retrieves the user name as known to this database.
	 *
	 * @return the database user name
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getUserName() throws SQLException {
		return "default";
	}
	
	/** Retrieves a description of a table's columns that are automatically
	 * updated when any value in a row is updated.  They are
	 * unordered.
	 *
	 * <P>Each column description has the following columns:
	 *  <OL>
	 * 	<LI><B>SCOPE</B> short => is not used
	 * 	<LI><B>COLUMN_NAME</B> String => column name
	 * 	<LI><B>DATA_TYPE</B> short => SQL data type from <code>java.sql.Types</code>
	 * 	<LI><B>TYPE_NAME</B> String => Data source-dependent type name
	 * 	<LI><B>COLUMN_SIZE</B> int => precision
	 * 	<LI><B>BUFFER_LENGTH</B> int => length of column value in bytes
	 * 	<LI><B>DECIMAL_DIGITS</B> short	 => scale
	 * 	<LI><B>PSEUDO_COLUMN</B> short => whether this is pseudo column
	 *      like an Oracle ROWID
	 *      <UL>
	 *      <LI> versionColumnUnknown - may or may not be pseudo column
	 *      <LI> versionColumnNotPseudo - is NOT a pseudo column
	 *      <LI> versionColumnPseudo - is a pseudo column
	 *      </UL>
	 *  </OL>
	 *
	 * @param catalog a catalog name; must match the catalog name as it
	 *        is stored in the database; "" retrieves those without a catalog;
	 *        <code>null</code> means that the catalog name should not be used to narrow
	 *        the search
	 * @param schema a schema name; must match the schema name
	 *        as it is stored in the database; "" retrieves those without a schema;
	 *        <code>null</code> means that the schema name should not be used to narrow
	 *        the search
	 * @param table a table name; must match the table name as it is stored
	 *        in the database
	 * @return a <code>ResultSet</code> object in which each row is a
	 *         column description
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public java.sql.ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
		return null;
	}
	
	/** Retrieves whether or not a visible row insert can be detected
	 * by calling the method <code>ResultSet.rowInserted</code>.
	 *
	 * @param type the <code>ResultSet</code> type; one of
	 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @return <code>true</code> if changes are detected by the specified result
	 *         set type; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public boolean insertsAreDetected(int type) throws SQLException {
		return false;
	}
	
	/** Retrieves whether a catalog appears at the start of a fully qualified
	 * table name.  If not, the catalog appears at the end.
	 *
	 * @return <code>true</code> if the catalog name appears at the beginning
	 *         of a fully qualified table name; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isCatalogAtStart() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database is in read-only mode.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isReadOnly() throws SQLException {
		return this.conn.getConnectionManager().getTableManager().isReadOnly();
	}
	
	/** Indicates whether updates made to a LOB are made on a copy or directly
	 * to the LOB.
	 * @return <code>true</code> if updates are made to a copy of the LOB;
	 *         <code>false</code> if updates are made directly to the LOB
	 * @throws SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public boolean locatorsUpdateCopy() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports concatenations between
	 * <code>NULL</code> and non-<code>NULL</code> values being
	 * <code>NULL</code>.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean nullPlusNonNullIsNull() throws SQLException {
		return false;
	}
	
	/** Retrieves whether <code>NULL</code> values are sorted at the end regardless of
	 * sort order.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean nullsAreSortedAtEnd() throws SQLException {
		return false;
	}
	
	/** Retrieves whether <code>NULL</code> values are sorted at the start regardless
	 * of sort order.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean nullsAreSortedAtStart() throws SQLException {
		return false;
	}
	
	/** Retrieves whether <code>NULL</code> values are sorted high.
	 * Sorted high means that <code>NULL</code> values
	 * sort higher than any other value in a domain.  In an ascending order,
	 * if this method returns <code>true</code>,  <code>NULL</code> values
	 * will appear at the end. By contrast, the method
	 * <code>nullsAreSortedAtEnd</code> indicates whether <code>NULL</code> values
	 * are sorted at the end regardless of sort order.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean nullsAreSortedHigh() throws SQLException {
		return false;
	}
	
	/** Retrieves whether <code>NULL</code> values are sorted low.
	 * Sorted low means that <code>NULL</code> values
	 * sort lower than any other value in a domain.  In an ascending order,
	 * if this method returns <code>true</code>,  <code>NULL</code> values
	 * will appear at the beginning. By contrast, the method
	 * <code>nullsAreSortedAtStart</code> indicates whether <code>NULL</code> values
	 * are sorted at the beginning regardless of sort order.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean nullsAreSortedLow() throws SQLException {
		return false;
	}
	
	/** Retrieves whether deletes made by others are visible.
	 *
	 * @param type the <code>ResultSet</code> type; one of
	 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @return <code>true</code> if deletes made by others
	 *        are visible for the given result set type;
	 *        <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public boolean othersDeletesAreVisible(int type) throws SQLException {
		return false;
	}
	
	/** Retrieves whether inserts made by others are visible.
	 *
	 * @param type the <code>ResultSet</code> type; one of
	 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @return <code>true</code> if inserts made by others
	 *         are visible for the given result set type;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public boolean othersInsertsAreVisible(int type) throws SQLException {
		return false;
	}
	
	/** Retrieves whether updates made by others are visible.
	 *
	 * @param type the <code>ResultSet</code> type; one of
	 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @return <code>true</code> if updates made by others
	 *        are visible for the given result set type;
	 *        <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		return false;
	}
	
	/** Retrieves whether a result set's own deletes are visible.
	 *
	 * @param type the <code>ResultSet</code> type; one of
	 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @return <code>true</code> if deletes are visible for the given result set type;
	 *        <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public boolean ownDeletesAreVisible(int type) throws SQLException {
		return false;
	}
	
	/** Retrieves whether a result set's own inserts are visible.
	 *
	 * @param type the <code>ResultSet</code> type; one of
	 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @return <code>true</code> if inserts are visible for the given result set type;
	 *        <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public boolean ownInsertsAreVisible(int type) throws SQLException {
		return false;
	}
	
	/**
	 * Retrieves whether for the given type of <code>ResultSet</code> object,
	 * the result set's own updates are visible.
	 *
	 * @param type the <code>ResultSet</code> type; one of
	 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @return <code>true</code> if updates are visible for the given result set type;
	 *        <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database treats mixed case unquoted SQL identifiers as
	 * case insensitive and stores them in lower case.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database treats mixed case quoted SQL identifiers as
	 * case insensitive and stores them in lower case.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database treats mixed case unquoted SQL identifiers as
	 * case insensitive and stores them in mixed case.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database treats mixed case quoted SQL identifiers as
	 * case insensitive and stores them in mixed case.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database treats mixed case unquoted SQL identifiers as
	 * case insensitive and stores them in upper case.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database treats mixed case quoted SQL identifiers as
	 * case insensitive and stores them in upper case.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the ANSI92 entry level SQL
	 * grammar.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the ANSI92 full SQL grammar supported.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsANSI92FullSQL() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the ANSI92 intermediate SQL grammar supported.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports <code>ALTER TABLE</code>
	 * with add column.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports <code>ALTER TABLE</code>
	 * with drop column.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports batch updates.
	 *
	 * @return <code>true</code> if this database supports batch upcates;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public boolean supportsBatchUpdates() throws SQLException {
		return false;
	}
	
	/** Retrieves whether a catalog name can be used in a data manipulation statement.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return false;
	}
	
	/** Retrieves whether a catalog name can be used in an index definition statement.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return false;
	}
	
	/** Retrieves whether a catalog name can be used in a privilege definition statement.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return false;
	}
	
	/** Retrieves whether a catalog name can be used in a procedure call statement.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return false;
	}
	
	/** Retrieves whether a catalog name can be used in a table definition statement.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports column aliasing.
	 *
	 * <P>If so, the SQL AS clause can be used to provide names for
	 * computed columns or to provide alias names for columns as
	 * required.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsColumnAliasing() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the <code>CONVERT</code>
	 * function between SQL types.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsConvert() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the <code>CONVERT</code>
	 * for two given SQL types.
	 *
	 * @param fromType the type to convert from; one of the type codes from
	 *        the class <code>java.sql.Types</code>
	 * @param toType the type to convert to; one of the type codes from
	 *        the class <code>java.sql.Types</code>
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @see Types
	 *
	 */
	public boolean supportsConvert(int fromType, int toType) throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the ODBC Core SQL grammar.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsCoreSQLGrammar() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports correlated subqueries.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports both data definition and
	 * data manipulation statements within a transaction.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports only data manipulation
	 * statements within a transaction.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		return false;
	}
	
	/** Retrieves whether, when table correlation names are supported, they
	 * are restricted to being different from the names of the tables.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports expressions in
	 * <code>ORDER BY</code> lists.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the ODBC Extended SQL grammar.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports full nested outer joins.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsFullOuterJoins() throws SQLException {
		return false;
	}
	
	/** Retrieves whether auto-generated keys can be retrieved after
	 * a statement has been executed.
	 *
	 * @return <code>true</code> if auto-generated keys can be retrieved
	 *         after a statement has executed; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public boolean supportsGetGeneratedKeys() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports some form of
	 * <code>GROUP BY</code> clause.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsGroupBy() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports using columns not included in
	 * the <code>SELECT</code> statement in a <code>GROUP BY</code> clause
	 * provided that all of the columns in the <code>SELECT</code> statement
	 * are included in the <code>GROUP BY</code> clause.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports using a column that is
	 * not in the <code>SELECT</code> statement in a
	 * <code>GROUP BY</code> clause.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsGroupByUnrelated() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the SQL Integrity
	 * Enhancement Facility.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports specifying a
	 * <code>LIKE</code> escape clause.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsLikeEscapeClause() throws SQLException {
		return true;
	}
	
	/** Retrieves whether this database provides limited support for outer
	 * joins.  (This will be <code>true</code> if the method
	 * <code>supportsFullOuterJoins</code> returns <code>true</code>).
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsLimitedOuterJoins() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the ODBC Minimum SQL grammar.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database treats mixed case unquoted SQL identifiers as
	 * case sensitive and as a result stores them in mixed case.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database treats mixed case quoted SQL identifiers as
	 * case sensitive and as a result stores them in mixed case.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return false;
	}
	
	/** Retrieves whether it is possible to have multiple <code>ResultSet</code> objects
	 * returned from a <code>CallableStatement</code> object
	 * simultaneously.
	 *
	 * @return <code>true</code> if a <code>CallableStatement</code> object
	 *         can return multiple <code>ResultSet</code> objects
	 *         simultaneously; <code>false</code> otherwise
	 * @exception SQLException if a datanase access error occurs
	 * @since 1.4
	 *
	 */
	public boolean supportsMultipleOpenResults() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports getting multiple
	 * <code>ResultSet</code> objects from a single call to the
	 * method <code>execute</code>.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsMultipleResultSets() throws SQLException {
		return true;
	}
	
	/** Retrieves whether this database allows having multiple
	 * transactions open at once (on different connections).
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsMultipleTransactions() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports named parameters to callable
	 * statements.
	 *
	 * @return <code>true</code> if named parameters are supported;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public boolean supportsNamedParameters() throws SQLException {
		return false;
	}
	
	/** Retrieves whether columns in this database may be defined as non-nullable.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsNonNullableColumns() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports keeping cursors open
	 * across commits.
	 *
	 * @return <code>true</code> if cursors always remain open;
	 *       <code>false</code> if they might not remain open
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports keeping cursors open
	 * across rollbacks.
	 *
	 * @return <code>true</code> if cursors always remain open;
	 *       <code>false</code> if they might not remain open
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports keeping statements open
	 * across commits.
	 *
	 * @return <code>true</code> if statements always remain open;
	 *       <code>false</code> if they might not remain open
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports keeping statements open
	 * across rollbacks.
	 *
	 * @return <code>true</code> if statements always remain open;
	 *       <code>false</code> if they might not remain open
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports using a column that is
	 * not in the <code>SELECT</code> statement in an
	 * <code>ORDER BY</code> clause.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsOrderByUnrelated() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports some form of outer join.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsOuterJoins() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports positioned <code>DELETE</code>
	 * statements.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsPositionedDelete() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports positioned <code>UPDATE</code>
	 * statements.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsPositionedUpdate() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the given concurrency type
	 * in combination with the given result set type.
	 *
	 * @param type defined in <code>java.sql.ResultSet</code>
	 * @param concurrency type defined in <code>java.sql.ResultSet</code>
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @see Connection
	 * @since 1.2
	 *
	 */
	public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the given result set holdability.
	 *
	 * @param holdability one of the following constants:
	 *          <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
	 *          <code>ResultSet.CLOSE_CURSORS_AT_COMMIT<code>
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @see Connection
	 * @since 1.4
	 *
	 */
	public boolean supportsResultSetHoldability(int holdability) throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the given result set type.
	 *
	 * @param type defined in <code>java.sql.ResultSet</code>
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @see Connection
	 * @since 1.2
	 *
	 */
	
	public boolean supportsResultSetType(int type) throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports savepoints.
	 *
	 * @return <code>true</code> if savepoints are supported;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.4
	 *
	 */
	public boolean supportsSavepoints() throws SQLException {
		return false;
	}
	
	/** Retrieves whether a schema name can be used in a data manipulation statement.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return false;
	}
	
	/** Retrieves whether a schema name can be used in an index definition statement.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return false;
	}
	
	/** Retrieves whether a schema name can be used in a privilege definition statement.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return false;
	}
	
	/** Retrieves whether a schema name can be used in a procedure call statement.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		return false;
	}
	
	/** Retrieves whether a schema name can be used in a table definition statement.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports <code>SELECT FOR UPDATE</code>
	 * statements.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsSelectForUpdate() throws SQLException {
		return false;
	}
	
	/** Retrieves weather this database supports statement pooling.
	 *
	 * @return <code>true</code> is so;
	 *         <code>false</code> otherwise
	 * @throws SQLExcpetion if a database access error occurs
	 * @since 1.4
	 *
	 */
	public boolean supportsStatementPooling() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports stored procedure calls
	 * that use the stored procedure escape syntax.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsStoredProcedures() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports subqueries in comparison
	 * expressions.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports subqueries in
	 * <code>EXISTS</code> expressions.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsSubqueriesInExists() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports subqueries in
	 * <code>IN</code> statements.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsSubqueriesInIns() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports subqueries in quantified
	 * expressions.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports table correlation names.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsTableCorrelationNames() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports the given transaction isolation level.
	 *
	 * @param level one of the transaction isolation levels defined in
	 *         <code>java.sql.Connection</code>
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @see Connection
	 *
	 */
	public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports transactions. If not, invoking the
	 * method <code>commit</code> is a noop, and the isolation level is
	 * <code>TRANSACTION_NONE</code>.
	 *
	 * @return <code>true</code> if transactions are supported;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsTransactions() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports SQL <code>UNION</code>.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsUnion() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database supports SQL <code>UNION ALL</code>.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean supportsUnionAll() throws SQLException {
		return false;
	}
	
	/** Retrieves whether or not a visible row update can be detected by
	 * calling the method <code>ResultSet.rowUpdated</code>.
	 *
	 * @param type the <code>ResultSet</code> type; one of
	 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
	 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
	 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
	 * @return <code>true</code> if changes are detected by the result set type;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public boolean updatesAreDetected(int type) throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database uses a file for each table.
	 *
	 * @return <code>true</code> if this database uses a local file for each table;
	 *         <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean usesLocalFilePerTable() throws SQLException {
		return false;
	}
	
	/** Retrieves whether this database stores tables in a local file.
	 *
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean usesLocalFiles() throws SQLException {
		return false;
	}

	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public ResultSet getClientInfoProperties() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet getFunctionColumns(String catalog, String schemaPattern,
			String functionNamePattern, String columnNamePattern)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet getFunctions(String catalog, String schemaPattern,
			String functionNamePattern) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowIdLifetime getRowIdLifetime() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet getSchemas(String catalog, String schemaPattern)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
