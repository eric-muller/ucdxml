<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:lp="http://unicode.org/ns/2001/lp"
  xmlns="http://www.w3.org/TR/REC-html40"
  version="2.0">

<xsl:output 
  method="text"
  encoding="UTF-8"/>
 
<xsl:param name='targetdir'>.</xsl:param>

<xsl:template match='/'>
  <xsl:apply-templates select='/descendant::lp:scrap[@file]'/>
</xsl:template>

<xsl:template match='lp:scrap[@revisionflag="deleted"]'/>

<xsl:template match='lp:scrap[@file]'>
  <xsl:message>sending to <xsl:value-of select='$targetdir'/>/<xsl:value-of select='@file'/></xsl:message>
  <xsl:result-document href="{$targetdir}/{@file}" method="{@method}">
    # Copyright &#x00A9; <xsl:value-of select='/article/articleinfo/copyright/year'/> Unicode, Inc.

    <xsl:apply-templates/>
  </xsl:result-document>
</xsl:template>

<xsl:key name='scrap-key' match='lp:scrap' use='@id'/>

<xsl:template match='lp:include'>
  <xsl:apply-templates select='key("scrap-key", @linkend)'/>
</xsl:template>

<xsl:template match='phrase[@revisionflag="deleted"]'/>

<xsl:template match='phrase[@revisionflag="added"]'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='@* | node()'>
  <xsl:copy>
    <xsl:apply-templates select='@* | node()'/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
