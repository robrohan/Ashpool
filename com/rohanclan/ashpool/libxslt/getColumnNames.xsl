<?xml version="1.0" encoding="utf-8"?>
<!--
	Author: Rob Rohan
	Master: Ashpool XML Database
	File: getColumnNames.xsl
	Date: 2003.02.18
	Purpose: gets a resultset ready list of columns from an xml
		document in table form. The first 'rows' column 
		names.
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
<xsl:output method="xml" indent="no" encoding="utf-8"/>

	<xsl:param name="tablename" select="'emptytablename'"/>
	<xsl:param name="defdatatype" select="'12'"/>
	
	<xsl:template match="/">
	<t>
		<xsl:apply-templates select="*/*[1]"/>
	</t>
	</xsl:template>

	<xsl:template match="*/*">
	<xsl:call-template name="collist">
		<xsl:with-param name="row" select="./*"/>
	</xsl:call-template>
	</xsl:template>

	<xsl:template name="collist">
		<xsl:param name="row" select="''"/>
		<xsl:for-each select="$row">
		<r>	
			<xsl:variable name="nodename" select="name()"/>
			<xsl:variable name="curpos" select="position()"/>
			<xsl:element name="COLUMN_NAME">
				<xsl:value-of select="$nodename"/>
			</xsl:element>

			<xsl:element name="TABLE_SCHEM"/>
			<xsl:element name="TABLE_NAME">
				<xsl:value-of select="$tablename"/>
			</xsl:element>
			<xsl:element name="DATA_TYPE">
				<xsl:value-of select="$defdatatype"/>
			</xsl:element>
			<xsl:element name="TYPE_NAME">string</xsl:element>
			<xsl:element name="COLUMN_SIZE"/>
			<xsl:element name="BUFFER_LENGTH"/>
			<xsl:element name="DECIMAL_DIGITS"/>
			<xsl:element name="NUM_PREC_RADIX"/>
			<xsl:element name="NULLABLE">1</xsl:element>
			<xsl:element name="REMARKS">NO SCHEMA</xsl:element>
			<xsl:element name="COLUMN_DEF"/>
			<xsl:element name="SQL_DATA_TYPE"/>
			<xsl:element name="SQL_DATETIME_SUB"/>
			<xsl:element name="CHAR_OCTET_LENGTH"/>
			<xsl:element name="ORDINAL_POSITION">1</xsl:element>
			<xsl:element name="IS_NULLABLE">YES</xsl:element>
			<xsl:element name="SCOPE_CATLOG"/>
			<xsl:element name="SCOPE_SCHEMA"/>
			<xsl:element name="SCOPE_TABLE"/>
			<xsl:element name="SOURCE_DATA_TYPE"/>
			<xsl:element name="AUTO_NUMBER">NO</xsl:element>
		</r>
		</xsl:for-each>	

	</xsl:template>

	<xsl:template match="text()"/>
</xsl:stylesheet>
