<?xml version="1.0" encoding="utf-8"?>
<!--
	Author: Rob Rohan
	File: doDistinct.xsl
	Date: 2003-03-24
	Purpose: Removes all the duplicates from a node set. Used for much
		of the aggregate functions, and for the distinct keyword.
		NOTE: this process addes a second sweep on a result.
-->
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:set="http://exslt.org/sets"
	extension-element-prefixes="set"
>
<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	<xsl:template match="/">
	<!-- get the proper table name -->
	<xsl:variable name="tname">
		<xsl:value-of select="name(/*)"/>
	</xsl:variable>

	<xsl:element name="{$tname}">
		<xsl:apply-templates select="set:distinct(*/*)"/>
	</xsl:element>
	</xsl:template>

	<xsl:template match="*/*">
	<!-- get the proper row name -->
	<!-- <xsl:variable name="rname">
		<xsl:value-of select="name(.)"/>
	</xsl:variable>
	
	<xsl:element name="{$rname}"> -->
		<xsl:copy-of select="."/>
	<!-- </xsl:element> -->
	
	</xsl:template>

	<xsl:template match="text()"/>
</xsl:stylesheet>
