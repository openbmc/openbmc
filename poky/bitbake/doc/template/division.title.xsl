<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:d="http://docbook.org/ns/docbook"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="d">
  
  <xsl:template name="division.title">
    <xsl:param name="node" select="."/>
    
    <h1>
      <xsl:attribute name="class">title</xsl:attribute>
      <xsl:call-template name="anchor">
        <xsl:with-param name="node" select="$node"/>
        <xsl:with-param name="conditional" select="0"/>
      </xsl:call-template>
      <xsl:apply-templates select="$node" mode="object.title.markup">
        <xsl:with-param name="allow-anchors" select="1"/>
      </xsl:apply-templates>
      <xsl:call-template name="permalink">
        <xsl:with-param name="node" select="$node"/>
      </xsl:call-template>
    </h1>
  </xsl:template>
</xsl:stylesheet>

