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
 * AResultSetMetaData.java
 *
 * Created on February 2, 2003, 5:49 AM
 */

package com.rohanclan.ashpool.core;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/** 
 * Metadata object for a AResultSet
 * @author Rob Rohan
 */
public class AResultSetMetaData implements java.sql.ResultSetMetaData {
	
	private AResultSet rs;
	
	/** Creates a new instance of AResultSetMetaData */
	public AResultSetMetaData() {;}
	
	public AResultSetMetaData(AResultSet prs){
		this.rs = prs; 
	}
	
	/** Gets the designated column's table's catalog name.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return the name of the catalog for the table in which the given column
	 *          appears or "" if not applicable
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getCatalogName(int column) throws SQLException {
		return "";
	}
	
	/** <p>Returns the fully-qualified name of the Java class whose instances
	 * are manufactured if the method <code>ResultSet.getObject</code>
	 * is called to retrieve a value
	 * from the column.  <code>ResultSet.getObject</code> may return a subclass of the
	 * class returned by this method.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return the fully-qualified name of the class in the Java programming
	 *         language that would be used by the method
	 * <code>ResultSet.getObject</code> to retrieve the value in the specified
	 * column. This is the class name used for custom mapping.
	 * @exception SQLException if a database access error occurs
	 * @since 1.2
	 *
	 */
	public String getColumnClassName(int column) throws SQLException {
		return "";
	}
	
	/** Returns the number of columns in this <code>ResultSet</code> object.
	 *
	 * @return the number of columns
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getColumnCount() throws SQLException {
		return rs.getResultTable().size();
	}
	
	public int getRecordCount() throws SQLException {
		//they should all be the same
		//int size;
		if(!rs.getResultTable().isEmpty()){
			return ((ResultColumn)rs.getResultTable().get(0)).columnData.size();
		}else{
			return 0;
		}
	}
	
	/** Indicates the designated column's normal maximum width in characters.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return the normal maximum number of characters allowed as the width
	 *          of the designated column
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getColumnDisplaySize(int column) throws SQLException {
		return 20;
	}
	
	/** Gets the designated column's suggested title for use in printouts and
	 * displays.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return the suggested column title
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getColumnLabel(int column) throws SQLException {
		return ((ResultColumn)rs.getResultTable().get(column-1)).columnName;
	}
	
	/** Get the designated column's name.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return column name
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getColumnName(int column) throws SQLException {
		return ((ResultColumn)rs.getResultTable().get(column-1)).columnName;
	}
	
	/** Retrieves the designated column's SQL type.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return SQL type from java.sql.Types
	 * @exception SQLException if a database access error occurs
	 * @see Types
	 *
	 */
	public int getColumnType(int column) throws SQLException {
		return ((ResultColumn)rs.getResultTable().get(column-1)).type;
	}
	
	/** Retrieves the designated column's database-specific type name.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return type name used by the database. If the column type is
	 * a user-defined type, then a fully-qualified type name is returned.
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getColumnTypeName(int column) throws SQLException {
		return "undefined";
	}
	
	/** Get the designated column's number of decimal digits.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return precision
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getPrecision(int column) throws SQLException {
		return 0;
	}
	
	/** Gets the designated column's number of digits to right of the decimal point.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return scale
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int getScale(int column) throws SQLException {
		return 0;
	}
	
	/** Get the designated column's table's schema.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return schema name or "" if not applicable
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getSchemaName(int column) throws SQLException {
		return "no schema";
	}
	
	/** Gets the designated column's table name.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return table name or "" if not applicable
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public String getTableName(int column) throws SQLException {
		return "undefined";
	}
	
	/** Indicates whether the designated column is automatically numbered, thus read-only.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isAutoIncrement(int column) throws SQLException {
		return false;
	}
	
	/** Indicates whether a column's case matters.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isCaseSensitive(int column) throws SQLException {
		return true;
	}
	
	/** Indicates whether the designated column is a cash value.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isCurrency(int column) throws SQLException {
		return false;
	}
	
	/** Indicates whether a write on the designated column will definitely succeed.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isDefinitelyWritable(int column) throws SQLException {
		return true;
	}
	
	/** Indicates the nullability of values in the designated column.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return the nullability status of the given column; one of <code>columnNoNulls</code>,
	 *          <code>columnNullable</code> or <code>columnNullableUnknown</code>
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public int isNullable(int column) throws SQLException {
		return ResultSetMetaData.columnNullable;
	}
	
	/** Indicates whether the designated column is definitely not writable.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isReadOnly(int column) throws SQLException {
		return false;
	}
	
	/** Indicates whether the designated column can be used in a where clause.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isSearchable(int column) throws SQLException {
		return true;
	}
	
	/** Indicates whether values in the designated column are signed numbers.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isSigned(int column) throws SQLException {
		return false;
	}
	
	/** Indicates whether it is possible for a write on the designated column to succeed.
	 *
	 * @param column the first column is 1, the second is 2, ...
	 * @return <code>true</code> if so; <code>false</code> otherwise
	 * @exception SQLException if a database access error occurs
	 *
	 */
	public boolean isWritable(int column) throws SQLException {
		return true;
	}
}
