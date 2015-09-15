<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:d="http://docbook.org/ns/docbook"
  xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="d">

  <xsl:template name="section.title">
    <xsl:variable name="section"
      select="(ancestor::section |
               ancestor::simplesect|
               ancestor::sect1|
               ancestor::sect2|
               ancestor::sect3|
               ancestor::sect4|
               ancestor::sect5)[last()]"/>

    <xsl:variable name="renderas">
      <xsl:choose>
        <xsl:when test="$section/@renderas = 'sect1'">1</xsl:when>
        <xsl:when test="$section/@renderas = 'sect2'">2</xsl:when>
        <xsl:when test="$section/@renderas = 'sect3'">3</xsl:when>
        <xsl:when test="$section/@renderas = 'sect4'">4</xsl:when>
        <xsl:when test="$section/@renderas = 'sect5'">5</xsl:when>
        <xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="level">
      <xsl:choose>
        <xsl:when test="$renderas != ''">
          <xsl:value-of select="$renderas"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="section.level">
            <xsl:with-param name="node" select="$section"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:call-template name="section.heading">
      <xsl:with-param name="section" select="$section"/>
      <xsl:with-param name="level" select="$level"/>
      <xsl:with-param name="title">
        <xsl:apply-templates select="$section" mode="object.title.markup">
          <xsl:with-param name="allow-anchors" select="1"/>
        </xsl:apply-templates>
        <xsl:if test="$level &gt; 0">
          <xsl:call-template name="permalink">
            <xsl:with-param name="node" select="$section"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
</xsl:stylesheet>
