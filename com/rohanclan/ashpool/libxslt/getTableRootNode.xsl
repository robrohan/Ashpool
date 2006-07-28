<?xml version="1.0" encoding="utf-8"?>
<!--
	Author: Rob Rohan
	File: getTableRootNode
	Date: 2003-03-14
	Purpose: gets the top level node name from a table. Meaning
		the root nodes name.
-->
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
<xsl:output method="text" indent="no" encoding="utf-8"/>
	<xsl:template match="/"><xsl:value-of select="name(./*[1])"/></xsl:template>
</xsl:stylesheet>
