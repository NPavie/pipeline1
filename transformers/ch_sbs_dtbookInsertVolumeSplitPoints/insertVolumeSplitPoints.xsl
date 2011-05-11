<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:dtb="http://www.daisy.org/z3986/2005/dtbook/"	
		exclude-result-prefixes="dtb">
  
  <xsl:output method="xml" encoding="utf-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:preserve-space elements="code samp"/>
	
  <xsl:param name="number_of_volumes" select="1"/>

  <xsl:variable name="all_p" select="//dtb:p"/>
  <xsl:variable name="p_per_volume" select="ceiling(count($all_p) div number($number_of_volumes)) + 1"/>
  <xsl:variable name="split_nodes" select="$all_p[position() mod $p_per_volume = 0]"/>

  <xsl:template match="dtb:p">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
    <xsl:if test="some $node in $split_nodes satisfies current() is $node">
      <xsl:element name="div" namespace="http://www.daisy.org/z3986/2005/dtbook/">
	<xsl:attribute name="class">volume-split-point</xsl:attribute>
	<xsl:element name="p" namespace="http://www.daisy.org/z3986/2005/dtbook/"/>
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <!-- Copy all other elements and attributes -->
  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
