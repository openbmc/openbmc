<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:d="http://docbook.org/ns/docbook"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="d">
  
  <xsl:template name="formal.object.heading">
    <xsl:param name="object" select="."/>
    <xsl:param name="title">
      <xsl:apply-templates select="$object" mode="object.title.markup">
        <xsl:with-param name="allow-anchors" select="1"/>
      </xsl:apply-templates>
    </xsl:param>
    <p class="title">
      <b><xsl:copy-of select="$title"/></b>
      <xsl:call-template name="permalink">
        <xsl:with-param name="node" select="$object"/>
      </xsl:call-template>
    </p>
  </xsl:template>
</xsl:stylesheet>
