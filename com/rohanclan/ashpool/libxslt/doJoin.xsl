<?xml version="1.0" ?>
 		<!--"
 			Author: Rob Rohan"
 			File: doJoin.xsl"
 			Date: 2003.02.27"
 			Purpose: joins two Ashpool xml tables (documents) into one document"
 				that is in the proper format to go through XMLtoResultSetFilter"
 				"
 				This process assumes that t1 and t2 are already limited/sorted/etc"
 				datastore : path to the datastore"
 				j1        : join field 1 - the from column"
 				j2        : join field 2 - the to column"
 				t1        : table 1"
 				t2        : table 2 can be a ~ or # file"
 				type      : join type 'inner' or 'outer'"
 				dir       : join direction (not implemented 'left' or 'right')"
 		-->
 		<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 			<xsl:output method="xml" indent="yes" />
 			
 			<xsl:param name="datastore" select="'file:///mnt/fileserver/projects/Ashpool/10minutes'"/>
 			<xsl:param name="j1" select="'vend_id'"/>
 			<xsl:param name="j2" select="'vend_id'"/>
 			<xsl:param name="t1" select="'Vendors'"/>
 			<xsl:param name="t2" select="'Products'"/>
 			<xsl:param name="type" select="'outer'"/>
 			<xsl:param name="dir" select="'left'"/>
 			
 			<!-- if filtered and sorted before hand this should just work... I think... :) -->
 			<xsl:variable name="a" select="document(concat($datastore,'/',$t1,'.xml'))/*/*"/>
 			<xsl:variable name="b" select="document(concat($datastore,'/',$t2,'.xml'))/*/*"/>
 			
 			<xsl:template match="/">
 			<t>
 			<xsl:choose>
 				<xsl:when test="$type = 'inner'">
 				
 				<!-- inner join, make sure we have something to join to -->
 				<xsl:choose>
 					<xsl:when test="count($b) &gt; 0">
 					<xsl:comment> = inner join = </xsl:comment>
 					<!-- for every node in the first table -->
 					<xsl:for-each select="$a">
 						<xsl:variable name="pos" select="position()"/>
 						<!-- get one 'row' -->
 						<xsl:variable name="alResults" select="$a[$pos]/*"/>
 						<!-- get the value of the join from -->
 						<xsl:variable name="joiner0" select="$a[$pos]/*[name() = $j1]"/>
 						<!-- get the first result set in table 2 that matches the join from value -->
 						<xsl:variable name="blResults" select="$b[./*[name() = $j2] = $joiner0][1]/*"/>
 						
 						<!-- if both fragments have nodes (inner join) -->
 						<xsl:if test="count($alResults) &gt; 0 and count($blResults) &gt; 0">
 						<r>
 							<xsl:copy-of select="$alResults"/>
 							<xsl:copy-of select="$blResults"/>
 						</r>
 						</xsl:if>
 						
 					</xsl:for-each>
 					</xsl:when>
 					<!-- there is nothing in table 2 to join with -->
 					<xsl:otherwise>
 						<xsl:copy-of select="$a"/>
 					</xsl:otherwise>
 				</xsl:choose>
 				</xsl:when>
 				
 				<xsl:when test="$type = 'outer'">
 				<!-- outer join -->
 				<xsl:comment> = outer join model = </xsl:comment>
 					<xsl:for-each select="$a">
 						<xsl:variable name="pos" select="position()"/>
 						<!-- get one 'row' -->
 						<xsl:variable name="arResults" select="$a[$pos]/*"/>
 						<!-- get the value of the join from -->
 						<xsl:variable name="joiner0" select="$a[$pos]/*[name() = $j1]"/>
 						
 						<xsl:call-template name="doOuterJoin">
 							<xsl:with-param name="join_row_count" select="count($b[./*[name() = $j2] = $joiner0])"/>
 							<xsl:with-param name="arResults" select="$arResults"/>
 							<xsl:with-param name="joiner0" select="$joiner0"/>
	 						<xsl:with-param name="current_row" select="1"/>
 						</xsl:call-template>
 					</xsl:for-each>
 				</xsl:when>
 				
 				<!-- bad type -->
 				<xsl:otherwise>
 					<xsl:message terminate="yes">
 					doJoin exception: unknown 'type' parameter. Valid types are 'inner' or 'outer'
 					</xsl:message>
 				</xsl:otherwise>
 			</xsl:choose>
 			</t>
 			</xsl:template>
 			
 			<xsl:template name="doOuterJoin">
 				<xsl:param name="join_row_count" select="0"/>
 				<xsl:param name="arResults" select="''"/>
 				<xsl:param name="current_row" select="1"/>
 				<xsl:param name="joiner0" select="''"/>
 				
 				<xsl:variable name="brResults" select="$b[./*[name() = $j2] = $joiner0][$current_row]/*"/>
 				
 				<xsl:if test="$arResults[$joiner0][1] != ''">
 				<r>
 				<!-- all of this row from table 1 -->
 				<xsl:copy-of select="$arResults"/>
 				<xsl:choose>
 					<!-- if there is a matching table 1 record show it"
 						otherwise get a blank copy of the first 'row'"
 					-->
 					<xsl:when test="count($brResults) &gt; 0">
 						<xsl:copy-of select="$brResults"/>
 					</xsl:when>
 					<xsl:otherwise>
 						<xsl:call-template name="padColumns">
 							<xsl:with-param name="examplecol" select="$b[1]/*"/>
 						</xsl:call-template>
 					</xsl:otherwise>
 					</xsl:choose>
 				</r>
 				</xsl:if>
 				
 				<!-- recall this template if current_row less then count -->
 				<xsl:if test="number($current_row) &lt; number($join_row_count)">
 					<xsl:call-template name="doOuterJoin">
 						<xsl:with-param name="join_row_count" select="$join_row_count"/>
 						<xsl:with-param name="arResults" select="$arResults"/>
 						<xsl:with-param name="joiner0" select="$joiner0"/>
 						<xsl:with-param name="current_row" select="$current_row + 1"/>
 					</xsl:call-template>
 				</xsl:if>
 			</xsl:template>
 			
 			
 			<!-- make an empty group of columns -->
 			<xsl:template name="padColumns">
 				<xsl:param name="examplecol" select="''"/>
 				
 				<xsl:for-each select="$examplecol">
 					<xsl:variable name="pos" select="position()"/>
 					<xsl:variable name="newname" select="name($examplecol[$pos])"/>
 					<xsl:element name="{$newname}"/>
 				</xsl:for-each>
 			</xsl:template>
 		</xsl:stylesheet>