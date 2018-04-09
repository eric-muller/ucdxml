<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:lp="http://unicode.org/ns/2001/lp"
  version="2.0">

<xsl:output 
  method="xml"
  encoding="utf-8"/>
 
<xsl:preserve-space elements='lp:scrap'/>

<xsl:template match='lp:scrap'>
  <para>
    <phrase revisionflag='{@revisionflag}'>
    <emphasis><anchor id='lp:{generate-id()}'>[<xsl:value-of select='@title'/>, <xsl:number count="lp:scrap" level="any"/>]</anchor> =</emphasis>
    <programlisting>
      <xsl:apply-templates mode='code-fragment'/>
    </programlisting>
    </phrase>
  </para>
</xsl:template>

<xsl:key name='scrap-key' match='lp:scrap' use='@id'/>

<xsl:template match='lp:include' mode='code-fragment'>
  <emphasis>
  <xsl:text>[</xsl:text>
      <classname>
        <xsl:choose>
          <xsl:when test='@title'>
            <xsl:value-of select='@title'/>
          </xsl:when>
          <xsl:when test='count (key ("scrap-key", @linkend)) = 1'>
	    <xsl:value-of select='key ("scrap-key", @linkend)/@title'/>
	  </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select='@linkend'/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text>: </xsl:text>
        <xsl:for-each select='key("scrap-key",@linkend)'>
          <link linkend='lp:{generate-id ()}'><xsl:number count='lp:scrap' level='any'/></link>
          <xsl:if test='position() != last ()'>, </xsl:if>
        </xsl:for-each>
      </classname>
  <xsl:text>]</xsl:text>
  </emphasis>
</xsl:template>


<!--xsl:template match='lp:include' mode='code-fragment'>
  <emphasis>
  <xsl:text>[</xsl:text>
  <xsl:choose>
    <xsl:when test='count(key ("scrap-key", @linkend))!=0'>
      <informaltable>
	<tgroup>
      <xsl:for-each select='key("scrap-key",@linkend)'>
        <row><entry>
        <link linkend='{generate-id()}'>
          <xsl:choose>
            <xsl:when test='@title'>
              <classname><xsl:value-of select='@title'/></classname>
            </xsl:when>
            <xsl:otherwise>
              <classname><xsl:value-of select='@linkend'/></classname>
            </xsl:otherwise>
          </xsl:choose>
        </link></entry></row>
      </xsl:for-each>
	</tgroup>
      </informaltable>
    </xsl:when>
    <xsl:otherwise>
      <classname>
        <xsl:choose>
          <xsl:when test='@title'>
            <xsl:value-of select='@title'/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select='@linkend'/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text>: </xsl:text>
        <xsl:for-each select='key("scrap-key",@linkend)'>
          <link linkend='{generate-id ()}'><xsl:number count='lp:scrap' level='any'/></link>
          <xsl:if test='position() != last ()'>, </xsl:if>
        </xsl:for-each>
      </classname>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:text>]</xsl:text>
  </emphasis>
</xsl:template-->


<xsl:template match='@* | node()'>
  <xsl:copy>
    <xsl:apply-templates select='@* | node()'/>
  </xsl:copy>
</xsl:template>

<xsl:template match='@* | node()' mode='code-fragment'>
  <xsl:copy>
    <xsl:apply-templates select='@* | node()'/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
