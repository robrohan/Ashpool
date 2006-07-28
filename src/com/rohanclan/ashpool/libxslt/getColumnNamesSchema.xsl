<?xml version="1.0" encoding="utf-8"?>
<!--
	Author: Rob Rohan
	File: getColumnNamesSchema.xsl
	Date: 2003.02.07
	Purpose: finds defined columns in a table schema mostly used
		for inserts.
-->
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xs"
>
	<xsl:output method="xml" indent="no" encoding="utf-8"/>
	
	<xsl:param name="tablename" select="'emptytablename'"/>
	<xsl:param name="schemaname" select="'emptyschemaname'"/>
	<xsl:param name="defdatatype" select="'12'"/>
	
	<xsl:template match="/">
	<t>
		<xsl:apply-templates />
	</t>
	</xsl:template>
	
	<xsl:template match="/xs:schema/xs:complexType/xs:sequence/xs:element">
	<r>
		<xsl:element name="COLUMN_NAME">
			<xsl:value-of select="@name" />
		</xsl:element>
		
		<xsl:element name="TABLE_SCHEM">
			<xsl:value-of select="$schemaname"/>
		</xsl:element>
		<xsl:element name="TABLE_NAME">
			<xsl:value-of select="$tablename"/>
		</xsl:element>
		<xsl:element name="DATA_TYPE">
			<xsl:call-template name="typeLookup">
				<xsl:with-param name="type" select="substring-after(@type,':')"/>
			</xsl:call-template>
		</xsl:element>
		<xsl:element name="TYPE_NAME">
			<xsl:value-of select="@type" />
		</xsl:element>
		<xsl:element name="COLUMN_SIZE">
			<xsl:value-of select="@maxLength" />
		</xsl:element>
		<xsl:element name="BUFFER_LENGTH"/>
		<xsl:element name="DECIMAL_DIGITS"/>
		<xsl:element name="NUM_PREC_RADIX"/>
		<xsl:element name="NULLABLE">
			<xsl:variable name="tf" select="@nillable"/>
			<xsl:choose>
				<xsl:when test="$tf = 'true'">1</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
		<xsl:element name="REMARKS">
			<xsl:value-of select="/xs:schema/xs:element/xs:annotation/xs:documentation"/>
		</xsl:element>
		<xsl:element name="COLUMN_DEF">
			<xsl:value-of select="@default"/>
		</xsl:element>
		<xsl:element name="SQL_DATA_TYPE"/>
		<xsl:element name="SQL_DATETIME_SUB"/>
		<xsl:element name="CHAR_OCTET_LENGTH"/>
		<xsl:element name="ORDINAL_POSITION">1</xsl:element>
		<xsl:element name="IS_NULLABLE">
			<xsl:variable name="tf" select="@nillable"/>
			<xsl:choose>
				<xsl:when test="$tf = 'true'">YES</xsl:when>
				<xsl:otherwise>NO</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
		<xsl:element name="SCOPE_CATLOG"/>
		<xsl:element name="SCOPE_SCHEMA"/>
		<xsl:element name="SCOPE_TABLE"/>
		<xsl:element name="SOURCE_DATA_TYPE"/>
		<xsl:element name="AUTO_NUMBER">
			<xsl:variable name="an" select="@autonumber"/>
			<xsl:choose>
				<xsl:when test="$an = 'true'">YES</xsl:when>
				<xsl:otherwise>NO</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</r>
	</xsl:template>
	
	<!-- turn a xs datatype into the java.sql.Types numeric value 
		(or close to it anyway ) -->
	<xsl:template name="typeLookup">
		<xsl:param name="type" select="'string'"/>
		<xsl:choose>
			<xsl:when test="$type = 'integer'">4</xsl:when>
			<xsl:when test="$type = 'string'">12</xsl:when>
			<xsl:when test="$type = 'boolean'">16</xsl:when>
			<xsl:when test="$type = 'decimal'">3</xsl:when>
			<xsl:when test="$type = 'float'">6</xsl:when>
			<xsl:when test="$type = 'double'">8</xsl:when>
			<xsl:when test="$type = 'dateTime'">93</xsl:when>
			<xsl:when test="$type = 'time'">92</xsl:when>
			<xsl:when test="$type = 'date'">91</xsl:when>
			<xsl:when test="$type = 'hexBinary'">-1</xsl:when>
			<xsl:when test="$type = 'base64Binary'">-1</xsl:when>
			<xsl:otherwise>12</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<xsl:template match="text()" />
	
</xsl:stylesheet>
