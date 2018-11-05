<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:unicode='http://unicode.org/ns/2001'
  xmlns='http://www.w3.org/1999/xhtml'
  exclude-result-prefixes='unicode'
  version="2.0">

<xsl:output 
  method="xml"
  omit-xml-declaration='yes'
  indent="yes"
  doctype-public='-//W3C//DTD XHTML 1.0 Transitional//EN'
  doctype-system='http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'
  encoding="UTF-8"/>
 

<xsl:template match='article'>
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      <link rel="stylesheet"
	    type="text/css"
	    href="http://www.unicode.org/reports/reports-v2.css"/>
      <title>
	<xsl:choose>
	  <xsl:when test='articleinfo/unicode:tr/@class="uax"'>
	    <xsl:text>UAX</xsl:text>
	  </xsl:when>
	  <xsl:when test='articleinfo/unicode:tr/@class="uts"'>
	    <xsl:text>UTS</xsl:text>
	  </xsl:when>
	  <xsl:when test='articleinfo/unicode:tr/@class="utr"'>
	    <xsl:text>UTR</xsl:text>
	  </xsl:when>
	</xsl:choose>
	<xsl:text> #</xsl:text>
	<xsl:value-of select='articleinfo/unicode:tr/@number'/>
	<xsl:text>: </xsl:text>
	<xsl:value-of select='title'/>
      </title>
    </head>
    <body style="background-color:#ffffff">

      <table class="header" cellpadding='0' cellspacing='0' width="100%">
	<tbody>
	  <tr>
	    <td class="icon"><a href="http://www.unicode.org/">
	    <img style='vertical-align:middle;border:0' alt="[Unicode]" src="http://www.unicode.org/webscripts/logo60s2.gif" height="33" width="34"/></a>&#x00A0;&#x00A0;<a class="bar" href="http://www.unicode.org/reports/">Technical Reports</a></td>
	  </tr>
	  <tr>
	    <td class="gray">&#x00A0;</td>
	  </tr>
	</tbody>
      </table>

      <div class="body"> 

	<xsl:if test='articleinfo/unicode:tr/@status = "working-draft"'>
	  <h2 style='text-align:center; background-color: #ffff00; border-style:dotted; border-width:1px'><i>Working draft of</i></h2>
	</xsl:if>

	<h2 style='text-align:center'>
          <span>
            <xsl:call-template name='AA'/>
            <xsl:call-template name='display-stage'/>
          </span>
	  <xsl:text> </xsl:text>
	  <xsl:choose>
	    <xsl:when test='articleinfo/unicode:tr/@class="uax"'>
	      <xsl:text>Unicode® Standard Annex</xsl:text>
	    </xsl:when>
	    <xsl:when test='articleinfo/unicode:tr/@class="uts"'>
	      <xsl:text>Unicode® Technical Standard</xsl:text>
	    </xsl:when>
	    <xsl:when test='articleinfo/unicode:tr/@class="utr"'>
	      <xsl:text>Unicode® Technical Report</xsl:text>
	    </xsl:when>
	  </xsl:choose>

	  <xsl:text> #</xsl:text>
	  <xsl:value-of select='articleinfo/unicode:tr/@number'/>
	</h2>

	<h1 style='text-align:center'><xsl:value-of select='title'/></h1>

	<xsl:call-template name='tracking'/>

	<h4 style='margin-top: 1em;'>Summary</h4>
	<xsl:apply-templates select='abstract'/>

	<xsl:call-template name='status'/>

	<xsl:call-template name='toc'/>

	<hr/>

	<xsl:apply-templates select='section|appendix|bibliography|acknowledgments'/>

	<h2><a name='Modifications'>Modifications</a></h2>
	<p>This section indicates the changes introduced by each
	revision.</p>
	<xsl:apply-templates select='articleinfo/revhistory'/>

	<hr/>

	<xsl:call-template name='copyright'/>

      </div>
    </body>
  </html>
</xsl:template>

<xsl:template name='display-stage'>
  <xsl:choose>
    <xsl:when test='articleinfo/unicode:tr/@stage="proposed-draft"'>
      <span style="color: #ff0000;"><xsl:text>Proposed Draft</xsl:text></span>
    </xsl:when>
    <xsl:when test='articleinfo/unicode:tr/@stage="draft"'>
      <span style="color: #ff0000';"><xsl:text>Draft</xsl:text></span>
    </xsl:when>
    <xsl:when test='articleinfo/unicode:tr/@stage="proposed-update"'>
      <xsl:text>Proposed Update</xsl:text>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<!--__________________________________________________________________ titles -->

<xsl:template name='myparentid'>
  <xsl:choose>
    <xsl:when test='../@id'>
      <xsl:value-of select='../@id'/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select='generate-id()'/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template match='section/title'>
  <xsl:param name='id'>
    <xsl:call-template name='myparentid'/>
  </xsl:param>

  <xsl:element name='h{count(ancestor::section)+1}'>
    <a name='{$id}'>
      <xsl:number level='multiple' count='section' format='1.1'/>
      <xsl:text> </xsl:text>
      <xsl:apply-templates/>
    </a>
  </xsl:element>
</xsl:template>

<xsl:template match='appendix/title'>
  <xsl:param name='id'>
    <xsl:call-template name='myparentid'/>
  </xsl:param>

  <h2>
    <a name='{$id}'>
      <xsl:text>Appendix </xsl:text>
      <xsl:number level='multiple' count='appendix' format='A.1'/>
      <xsl:text>. </xsl:text>
      <xsl:apply-templates/>
    </a>
  </h2>
</xsl:template>

<xsl:template match='acknowledgments/title'>
  <xsl:param name='id'>
    <xsl:call-template name='myparentid'/>
  </xsl:param>

  <h2>
    <a name='{$id}'>
      <xsl:text>Acknowledgments</xsl:text>
      <xsl:apply-templates/>
    </a>
  </h2>
</xsl:template>



<!--___________________________________________________________________ lists -->

<xsl:template match='itemizedlist'>
  <ul>
    <xsl:apply-templates/>
  </ul>
</xsl:template>

<xsl:template match='orderedlist'>
  <ol>
    <xsl:apply-templates/>
  </ol>
</xsl:template>

<xsl:template match='listitem'>
  <li>
    <xsl:apply-templates/>
  </li>
</xsl:template>

<xsl:template match='para/itemizedlist/listemitem'>
  <xsl:apply-templates/>
</xsl:template>

<!--_____________________________________________________________________ TOC -->

<xsl:template name='toc'>
  <h4>Contents</h4>

  <ul class='toc'>

    <xsl:apply-templates mode='toc'/>

    <li>
      <a href='#Modifications'>Modifications</a>
    </li>
  </ul>
</xsl:template>

<xsl:template match='section|appendix|acknowledgments' mode='toc'>
  <li>
    <xsl:apply-templates select='title' mode='toc'/>
    <xsl:if test='section'>
      <ul class='toc'>
        <xsl:apply-templates select='section|appendix|acknowledgements' mode='toc'/>
      </ul>
    </xsl:if>
  </li>
</xsl:template>

<xsl:template match='section/title' mode='toc'>
  <xsl:param name='id'>
    <xsl:call-template name='myparentid'/>
  </xsl:param>

  <xsl:number level='multiple' count='section' format='1.1'/>
  <xsl:text>&#x00A0;&#x00A0;&#x00A0;&#x00A0; </xsl:text>
  <a href='#{$id}'>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match='appendix/title' mode='toc'>
  <xsl:param name='id'>
    <xsl:call-template name='myparentid'/>
  </xsl:param>

  <xsl:text>Appendix </xsl:text>
  <xsl:number level='multiple' count='appendix' format='A.1'/>
  <xsl:text>.&#x00A0; </xsl:text>
  <a href='#{$id}'>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match='acknowledgments/title' mode='toc'>
  <xsl:param name='id'>
    <xsl:call-template name='myparentid'/>
  </xsl:param>

  <a href='#{$id}'>
    <xsl:text>Acknowledgments</xsl:text>
  </a>
</xsl:template>

<xsl:template match='bibliography' mode='toc'>
  <li>
    <a href='#references'>References</a>
  </li>
</xsl:template>

<xsl:template match='*|text()' mode='toc'/>


<!--__________________________________________________________________________-->

<xsl:template match='para'>
  <p><xsl:apply-templates/></p>
</xsl:template>

<xsl:template match='article/abstract//para'>
  <p><i><xsl:apply-templates/></i></p>
</xsl:template>

<xsl:template match='para//para'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='ulink'>
  <a href='{@url}'>
    <xsl:if test='@type="newwindow"'>
      <xsl:attribute name='target'>_blank</xsl:attribute>
    </xsl:if>
    <xsl:choose>
      <xsl:when test='text()'>
	<xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
	<xsl:value-of select='@url'/>
      </xsl:otherwise>
    </xsl:choose>
  </a>
</xsl:template>


<xsl:template match='anchor'>
  <a name='{@id}'><xsl:apply-templates/></a>
</xsl:template>

<xsl:template match='link'>
  <a href='#{@linkend}'><xsl:apply-templates/></a>
</xsl:template>

<xsl:key name='id' match='section|biblioentry' use='@id'/>

<xsl:template match='xref'>
  <xsl:variable name='target' select='key("id",@linkend)[1]'/>

  <xsl:choose>
    <xsl:when test='local-name($target) = "biblioentry"'>
       <i>
	 <xsl:text>[</xsl:text>
	 <a href='#{@linkend}'>
	   <xsl:apply-templates select='$target/abbrev'/>
	 </a>
	 <xsl:text>]</xsl:text>
       </i>
    </xsl:when>
    <xsl:when test='local-name($target) = "section"'>
      <xsl:apply-templates select='$target/title' mode='ref'/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:text>??</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
 
<xsl:template match='section/title' mode='ref'>
  <xsl:param name='id'>
    <xsl:call-template name='myparentid'/>
  </xsl:param>

  <xsl:number level='multiple' count='section' format='1.1'/>
  <xsl:text>, </xsl:text>
  <a href='#{$id}'>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match='emphasis'>
  <i><xsl:apply-templates/></i>
</xsl:template>

<xsl:template match='sgmltag'>
  <tt><xsl:apply-templates/></tt>
</xsl:template>


<xsl:template name='AA'>
  <xsl:if test='//article/articleinfo/unicode:tr/@stage="proposed-update"'>
    <xsl:attribute name='style'>background-color: #ffff00; border-style:dotted; border-width:1px</xsl:attribute>
  </xsl:if>
</xsl:template>

<xsl:template match='phrase[@revisionflag="added"]'>
  <span style='background-color: #ffff00; border-style:dotted; border-width:1px'>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match='phrase[@revisionflag="changed"]'>
  <span style='background-color: #ffff00; border-style:dotted; border-width:1px'>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match='phrase[@revisionflag="deleted"]'>
  <span style='text-decoration:line-through; background-color: #ffff00; border-style:dotted; border-width:1px'>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match='phrase'>
  <xsl:apply-templates/>
</xsl:template>


<!--____________________________________________________________ bibliography -->



<xsl:template match='bibliography'>
  <h2><a name='references'>References</a></h2>

  <table class='nbwide' cellpadding="0" cellspacing='12'>
    <tbody>
      <xsl:apply-templates/>
    </tbody>
  </table>
</xsl:template>


<xsl:template match='biblioentry'>
  <tr>
    <td width='99' class="nb" valign="top">[<a name="{@id}"><xsl:value-of select='abbrev'/></a>]</td>
    <td class="nb" valign="top">
      <xsl:apply-templates select='title'/>
      <xsl:apply-templates select='subtitle'/>
      <xsl:apply-templates select='abstract'/>
    </td>
  </tr>
</xsl:template>

<xsl:template match='biblioentry/title'>
  <xsl:apply-templates/>
  <xsl:if test='count (following-sibling::*) != 0'>
   <br/>
  </xsl:if>
</xsl:template>

<xsl:template match='biblioentry/subtitle'>
  <xsl:apply-templates/>
  <xsl:if test='count (following-sibling::*) != 0'>
    <br/>
  </xsl:if>
</xsl:template>

<xsl:template match='biblioentry/abstract'>
  <i><xsl:apply-templates/></i>
  <xsl:if test='count (following-sibling::*) != 0'>
    <br/>
  </xsl:if>

</xsl:template>

<xsl:template match='biblioentry/abstract/para'>
  <xsl:apply-templates/>
</xsl:template>

<!--_________________________________________________________________________ -->

<xsl:template match='literal'>
  <tt><xsl:apply-templates/></tt>
</xsl:template>

<xsl:template match='literallayout/text()'>
  <xsl:call-template name='cr-replace'>
    <xsl:with-param name='text'>
      <xsl:call-template name='trim-cr'>
        <xsl:with-param name='text'>
          <xsl:call-template name='sp-replace'>
            <xsl:with-param name='text' select='.'/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:with-param>
  </xsl:call-template>
</xsl:template>

<!--xsl:template match='programlisting/text()'>
  <tt><xsl:call-template name='cr-replace'>
    <xsl:with-param name='text'>
      <xsl:call-template name='trim-cr'>
        <xsl:with-param name='text'>
          <xsl:call-template name='sp-replace'>
            <xsl:with-param name='text'>
              <xsl:call-template name='tab-replace'>
                <xsl:with-param name='text' select='.'/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:with-param>
  </xsl:call-template></tt>
</xsl:template-->

<xsl:template match='programlisting/text()'>
  <tt style='white-space: pre;'><xsl:value-of select='.'/></tt>
</xsl:template>

<xsl:template name='trim-cr'>
  <xsl:param name='text'/>
  <xsl:variable name="cr"><xsl:text>&#xa;</xsl:text></xsl:variable>
  <xsl:choose>
    <xsl:when test='starts-with($text,$cr) and position()=1'>
      <xsl:call-template name='trim-cr'>
        <xsl:with-param name='text' select='substring($text,2)'/>
      </xsl:call-template>
    </xsl:when>
    <xsl:when test='substring($text,string-length($text))=$cr and position()=last()'>
      <xsl:call-template name='trim-cr'>
        <xsl:with-param name='text' select='substring($text,1,string-length($text)-1)'/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select='$text'/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
      
<xsl:template name="cr-replace">
  <xsl:param name="text"/>
  <xsl:variable name="cr"><xsl:text>&#xa;</xsl:text></xsl:variable>
  <xsl:choose>
    <xsl:when test="contains($text,$cr)">
      <xsl:value-of select="substring-before($text,$cr)"/>
      <br/><xsl:text>&#xa;</xsl:text>
      <xsl:call-template name="cr-replace">
        <xsl:with-param name="text" select="substring-after($text,$cr)"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$text"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="sp-replace">
  <xsl:param name="text"/>
  <xsl:variable name="sp"><xsl:text> </xsl:text></xsl:variable>
  <xsl:choose>
    <xsl:when test="contains($text,$sp)">
      <xsl:value-of select="substring-before($text,$sp)"/>
      <xsl:text>&#160;</xsl:text>
      <xsl:call-template name="sp-replace">
        <xsl:with-param name="text" select="substring-after($text,$sp)"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$text"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="tab-replace">
  <xsl:param name="text"/>
  <xsl:variable name="sp"><xsl:text>	</xsl:text></xsl:variable>
  <xsl:choose>
    <xsl:when test="contains($text,$sp)">
      <xsl:value-of select="substring-before($text,$sp)"/>
      <xsl:text>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:text>
      <xsl:call-template name="tab-replace">
        <xsl:with-param name="text" select="substring-after($text,$sp)"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$text"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--__________________________________________________________________ tables -->


<xsl:template match='informaltable'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='tgroup'>
  <table style='border-width: 0px;' cellspacing="2" cellpadding="2">
    <xsl:apply-templates/>
  </table>

  <xsl:for-each select='.//footnote'>
    <xsl:number level='any' from='tgroup' format='a.'/>
    <xsl:apply-templates/>
  </xsl:for-each>
</xsl:template>

<xsl:template match='tgroup//footnote'>
  <sup><xsl:number level='any' from='tgroup' format='a'/></sup>
</xsl:template>
 
<xsl:template match='thead'>
  <thead><xsl:apply-templates/></thead>
</xsl:template>

<xsl:template match='tbody'>
  <tbody><xsl:apply-templates/></tbody>
</xsl:template>
  
<xsl:template match='row'>
  <tr><xsl:apply-templates/></tr>
</xsl:template>

  
<xsl:template name="colspec.colnum">
  <xsl:param name="colspec" select="."/>
  <xsl:choose>
    <xsl:when test="$colspec/@colnum">
      <xsl:value-of select="$colspec/@colnum"/>
    </xsl:when>
    <xsl:when test="$colspec/preceding-sibling::colspec">
      <xsl:variable name="prec.colspec.colnum">
        <xsl:call-template name="colspec.colnum">
          <xsl:with-param name="colspec"
                          select="$colspec/preceding-sibling::colspec[1]"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:value-of select="$prec.colspec.colnum + 1"/>
    </xsl:when>
    <xsl:otherwise>1</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="calculate.colspan">
  <xsl:param name="entry" select="."/>
  <xsl:variable name="namest" select="$entry/@namest"/>
  <xsl:variable name="nameend" select="$entry/@nameend"/>

  <xsl:variable name="scol">
    <xsl:call-template name="colspec.colnum">
      <xsl:with-param name="colspec"
                      select="$entry/ancestor::tgroup/colspec[@colname=$namest]"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="ecol">
    <xsl:call-template name="colspec.colnum">
      <xsl:with-param name="colspec"
                      select="$entry/ancestor::tgroup/colspec[@colname=$nameend]"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:value-of select="$ecol - $scol + 1"/>
</xsl:template>


<xsl:template match='entry'>
  <td valign='top' style='border-width: 0px;' >
    <xsl:if test="@align">
      <xsl:attribute name="align">
	<xsl:value-of select="@align"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@valign">
      <xsl:attribute name="valign">
	<xsl:value-of select="@valign"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@namest">
      <xsl:attribute name="colspan">
	<xsl:call-template name="calculate.colspan"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@morerows != '0'">
      <xsl:attribute name="rowspan">
	<xsl:value-of select="@morerows+1"/>
      </xsl:attribute>
    </xsl:if>
    
    <xsl:apply-templates/>
  </td>
</xsl:template>
  


<!--_______________________________________________________ document tracking -->

<xsl:template name='tracking'>
  <xsl:param name='thisnumber'>
    <xsl:value-of select='articleinfo/unicode:tr/@number'/>
  </xsl:param>

  <xsl:param name='thisrev'>
    <xsl:value-of select='articleinfo/revhistory/revision[1]/revnumber'/>
  </xsl:param>

  <xsl:param name='thisurl'>
    <xsl:text>http://www.unicode.org/reports/tr</xsl:text>
    <xsl:value-of select='$thisnumber'/>
    <xsl:text>/tr</xsl:text>
    <xsl:value-of select='$thisnumber'/>
    <xsl:text>-</xsl:text>
    <xsl:value-of select='$thisrev'/>
    <xsl:text>.html</xsl:text>
  </xsl:param>

  <xsl:param name='prevrev'>
    <xsl:value-of select='articleinfo/unicode:tr/@prevrev'/>
  </xsl:param>

  <xsl:param name='prevurl'>
    <xsl:text>http://www.unicode.org/reports/tr</xsl:text>
    <xsl:value-of select='$thisnumber'/>
    <xsl:text>/tr</xsl:text>
    <xsl:value-of select='$thisnumber'/>
    <xsl:text>-</xsl:text>
    <xsl:value-of select='$prevrev'/>
    <xsl:text>.html</xsl:text>
  </xsl:param>

  <xsl:param name='latesturl'>
    <xsl:text>http://www.unicode.org/reports/tr</xsl:text>
    <xsl:value-of select='$thisnumber'/>
    <xsl:text>/</xsl:text>
  </xsl:param>

  <xsl:param name='thisschema'>
    <xsl:text>http://www.unicode.org/reports/tr</xsl:text>
    <xsl:value-of select='$thisnumber'/>
    <xsl:text>/tr</xsl:text>
    <xsl:value-of select='$thisnumber'/>
    <xsl:text>-</xsl:text>
    <xsl:value-of select='$thisrev'/>
    <xsl:text>.rnc</xsl:text>
  </xsl:param>

  <table class="simple" width='90%'>
    <tbody>
      <xsl:if test='articleinfo/unicode:tr/@class="uts" or articleinfo/unicode:tr/@class="uax"'>
	<tr>
	  <td valign='top' width='20%'>Version</td>
	  <td valign='top'>
            <xsl:if test='//article/articleinfo/unicode:tr/@class="uax"'>Unicode </xsl:if>
            <span>
              <xsl:call-template name='AA'/>
              <xsl:value-of select='articleinfo/unicode:tr/@version'/>
            </span>
          </td>
	</tr>
      </xsl:if>
      <tr>
	<td valign='top'>Editor<xsl:if test="count(articleinfo/author)!=1">s</xsl:if></td>
	<td valign='top'>
	  <xsl:apply-templates select='articleinfo/author'/>
	</td>
      </tr>
      <tr>
	<td valign='top'>Date</td>
	<td valign='top'>
          <span>
            <xsl:call-template name='AA'/>
            <xsl:value-of select='articleinfo/revhistory/revision[1]/date'/>
          </span>
        </td>
      </tr>

      <tr>
	<td valign='top'>This Version</td>
	<td valign='top'>
          <span>
            <xsl:call-template name='AA'/>
            <a href='{$thisurl}'><xsl:value-of select='$thisurl'/></a>
          </span>
        </td>
      </tr>

      <tr>
	<td valign='top'>Previous Version</td>
	<td valign='top'>
	  <xsl:choose>
	    <xsl:when test='$prevrev = ""'>
	      <xsl:text>n/a</xsl:text>
	    </xsl:when>
	    <xsl:otherwise>
              <span>
                <xsl:call-template name='AA'/>
                <a href='{$prevurl}'><xsl:value-of select='$prevurl'/></a>
              </span>
	    </xsl:otherwise>
	  </xsl:choose>
	</td>
      </tr>
      
      <tr>
	<td valign='top'>Latest Version</td>
	<td valign='top'>
          <a href='{$latesturl}'><xsl:value-of select='$latesturl'/></a>
        </td>
      </tr>

      <tr>
        <td valign='top'>Latest Proposed Update</td>
        <td valign='top'>
          <a href='{$latesturl}proposed.html'><xsl:value-of select='$latesturl'/>proposed.html</a>
        </td>
      </tr>

      <xsl:if test='articleinfo/unicode:tr/@schema'>
	<tr>
	  <td valign='top'>Schema</td>
	  <td valign='top'>
            <span>
              <xsl:call-template name='AA'/>
              <a href='{$thisschema}'><xsl:value-of select='$thisschema'/></a>
            </span>
          </td>
	</tr>
      </xsl:if>

      <tr>
	<td valign='top'>Revision</td>
	<td valign='top'><a href="#Modifications">
          <span>
            <xsl:call-template name='AA'/>
            <xsl:value-of select='$thisrev'/>
          </span>
	</a></td>
      </tr>
    </tbody>
  </table>
</xsl:template>

<xsl:template match='author'>
  <xsl:value-of select='firstname'/>
  <xsl:text> </xsl:text>
  <xsl:value-of select='surname'/> 
  <xsl:apply-templates select='email'/><br/>
</xsl:template>

<xsl:template match='email'>
  <xsl:text> (</xsl:text>
  <a><xsl:attribute name='href'>mailto:<xsl:value-of select='.'/></xsl:attribute><xsl:value-of select='.'/></a>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template match='revision'>
  <div><xsl:if test='revnumber=6'><xsl:call-template name='AA'/></xsl:if>
  <p><b>Revision <xsl:value-of select='revnumber'/></b></p>
  <xsl:apply-templates select='revdescription'/>
  </div>
</xsl:template>


<!--__________________________________________________________________ status -->

<xsl:template name='status'>
  <h4><i>Status</i></h4>

  <xsl:choose>
    <xsl:when test='articleinfo/unicode:tr/@stage="approved"'>
      <p><i>This document has been reviewed by Unicode members and
      other interested parties, and has been approved for publication
      by the Unicode Consortium. This is a stable document and may be
      used as reference material or cited as a normative reference by
      other specifications.</i></p>
    </xsl:when>
    <xsl:otherwise>
      <p>
        <xsl:call-template name='AA'/>
      <i>This is a <b><span style='color:#ff0000'>draft</span></b>
      document which may be updated, replaced, or superseded by other
      documents at any time. Publication does not imply endorsement by
      the Unicode Consortium.  This is not a stable document; it is
      inappropriate to cite this document as other than a work in
      progress.</i></p>
    </xsl:otherwise>
  </xsl:choose>

  <xsl:choose>
    <xsl:when test='articleinfo/unicode:tr/@class="uax"'>
      <blockquote>
        <p><i><b>A Unicode Standard Annex (UAX)</b> forms an integral
        part of the Unicode Standard, but is published online as a
        separate document. The Unicode Standard may require
        conformance to normative content in a Unicode Standard Annex,
        if so specified in the Conformance chapter of that version of
        the Unicode Standard. The version number of a UAX document
        corresponds to the version of the Unicode Standard of which it
        forms a part.</i></p>
      </blockquote>

      <p>
        <i>Please submit corrigenda and other comments with the
      online reporting form [<a
      href='http://www.unicode.org/reporting.html'>Feedback</a>]. Related
      information that is useful in understanding this annex is found
      in Unicode Standard Annex #41, &#x201C;<a
      href="http://www.unicode.org/reports/tr41/tr41-23.html">Common
      References for Unicode Standard Annexes.</a>&#x201D; For the
      latest version of the Unicode Standard, see [<a
      href="http://www.unicode.org/versions/latest/">Unicode</a>]. For
      a list of current Unicode Technical Reports, see [<a
      href="http://www.unicode.org/reports/">Reports</a>]. For
      more information about versions of the Unicode Standard, see [<a
      href="http://www.unicode.org/versions/">Versions</a>]. For any errata which may apply to this annex, see [<a href="http://www.unicode.org/errata/">Errata</a>].</i></p>

    </xsl:when>
    <xsl:when test='articleinfo/unicode:tr/@class="uts"'>
      <blockquote>
	<p><i><b>A Unicode Technical Standard (UTS)</b> is an
	independent specification. Conformance to the Unicode Standard
	does not imply conformance to any UTS.</i></p>
      </blockquote>

      <p><i>Please submit corrigenda and other comments with the
      online reporting form [<a
      href='#biblio_feedback'>Feedback</a>]. Related information that
      is useful in understanding this document is found in <a
      href="#references">References</a>.  For the latest version of
      the Unicode Standard see [<a
      href="#biblio_unicode">Unicode</a>]. For a list of current
      Unicode Technical Reports see [<a
      href="#biblio_reports">Reports</a>]. For more information about
      versions of the Unicode Standard, see [<a
      href="#biblio_versions">Versions</a>].</i></p>

    </xsl:when>
    <xsl:when test='articleinfo/unicode:tr/@class="utr"'>
      <blockquote>
	<p><i><b>A Unicode Technical Report (UTR)</b> contains
	informative material. Conformance to the Unicode Standard does
	not imply conformance to any UTR. Other specifications,
	however, are free to make normative references to a
	UTR.</i></p>
      </blockquote>

      <p><i>Please submit corrigenda and other comments with the
      online reporting form [<a
      href='#biblio_feedback'>Feedback</a>]. Related information that
      is useful in understanding this document is found in <a
      href="#references">References</a>.  For the latest version of
      the Unicode Standard see [<a
      href="#biblio_unicode">Unicode</a>]. For a list of current
      Unicode Technical Reports see [<a
      href="#biblio_reports">Reports</a>]. For more information about
      versions of the Unicode Standard, see [<a
      href="#biblio_versions">Versions</a>].</i></p>

    </xsl:when>
  </xsl:choose>
</xsl:template>


<xsl:template name='copyright'>
  <p class='copyright'>&#xa9; <xsl:apply-templates
  select='articleinfo/copyright/year'/> Unicode, Inc. All Rights Reserved. The
  Unicode Consortium makes no expressed or implied warranty of any
  kind, and assumes no liability for errors or omissions. No liability
  is assumed for incidental and consequential damages in connection
  with or arising out of the use of the information or programs
  contained or accompanying this technical report. The Unicode <a
  href="http://www.unicode.org/copyright.html">Terms of Use</a>
  apply.</p>

  <p class='copyright'>Unicode and the Unicode logo are trademarks of
  Unicode, Inc., and are registered in some jurisdictions.</p>
</xsl:template>

</xsl:stylesheet>
