<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:d="http://docbook.org/ns/docbook"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="d">

  <xsl:template name="component.title">
    <xsl:param name="node" select="."/>

    <xsl:variable name="level">
      <xsl:choose>
        <xsl:when test="ancestor::d:section">
          <xsl:value-of select="count(ancestor::d:section)+1"/>
        </xsl:when>
        <xsl:when test="ancestor::sect5">6</xsl:when>
        <xsl:when test="ancestor::sect4">5</xsl:when>
        <xsl:when test="ancestor::sect3">4</xsl:when>
        <xsl:when test="ancestor::sect2">3</xsl:when>
        <xsl:when test="ancestor::sect1">2</xsl:when>
        <xsl:otherwise>1</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="h{$level+1}" namespace="http://www.w3.org/1999/xhtml">
      <xsl:attribute name="class">title</xsl:attribute>
      <xsl:if test="$generate.id.attributes = 0">
        <xsl:call-template name="anchor">
          <xsl:with-param name="node" select="$node"/>
          <xsl:with-param name="conditional" select="0"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:apply-templates select="$node" mode="object.title.markup">
        <xsl:with-param name="allow-anchors" select="1"/>
      </xsl:apply-templates>
      <xsl:call-template name="permalink">
        <xsl:with-param name="node" select="$node"/>
      </xsl:call-template>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
