<?xml version="1.0" encoding="utf-8"?>
<!--
	Author: Rob Rohan
	File: getTableRowNode
	Date: 2003-03-14
	Purpose: gets a tables row marking node. Meaning what
		the xml doc uses to mark 2nd level roots
-->
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
<xsl:output method="text" indent="no" encoding="utf-8"/>
	<xsl:template match="/"><xsl:value-of select="name(./*/*[1])"/></xsl:template>
</xsl:stylesheet>
