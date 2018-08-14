<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">

  <xsl:import href="http://docbook.sourceforge.net/release/xsl/current/fo/docbook.xsl" />

  <!-- check project-plan.sh for how this is generated, needed to tweak
       the cover page
    -->
  <xsl:include href="/tmp/titlepage.xsl"/>

  <!-- To force a page break in document, i.e per section add a
      <?hard-pagebreak?> tag.
  -->
 <xsl:template match="processing-instruction('hard-pagebreak')">
   <fo:block break-before='page' />
 </xsl:template>

  <!--Fix for defualt indent getting TOC all wierd..
      See http://sources.redhat.com/ml/docbook-apps/2005-q1/msg00455.html
      FIXME: must be a better fix
    -->
  <xsl:param name="body.start.indent" select="'0'"/>
  <!--<xsl:param name="title.margin.left" select="'0'"/>-->

  <!-- stop long-ish header titles getting wrapped -->
  <xsl:param name="header.column.widths">1 10 1</xsl:param>

  <!-- customise headers and footers a little -->

  <xsl:template name="head.sep.rule">
   <xsl:if test="$header.rule != 0">
     <xsl:attribute name="border-bottom-width">0.5pt</xsl:attribute>
     <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
     <xsl:attribute name="border-bottom-color">#999999</xsl:attribute>
   </xsl:if>
  </xsl:template>

  <xsl:template name="foot.sep.rule">
    <xsl:if test="$footer.rule != 0">
     <xsl:attribute name="border-top-width">0.5pt</xsl:attribute>
     <xsl:attribute name="border-top-style">solid</xsl:attribute>
     <xsl:attribute name="border-top-color">#999999</xsl:attribute>
    </xsl:if>
  </xsl:template>

  <xsl:attribute-set name="header.content.properties">
    <xsl:attribute name="color">#999999</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="footer.content.properties">
    <xsl:attribute name="color">#999999</xsl:attribute>
  </xsl:attribute-set>


  <!-- general settings -->

  <xsl:param name="fop1.extensions" select="1"></xsl:param>
  <xsl:param name="paper.type" select="'A4'"></xsl:param>
  <xsl:param name="section.autolabel" select="1"></xsl:param>
  <xsl:param name="body.font.family" select="'verasans'"></xsl:param>
  <xsl:param name="title.font.family" select="'verasans'"></xsl:param>
  <xsl:param name="monospace.font.family" select="'veramono'"></xsl:param>

</xsl:stylesheet>
