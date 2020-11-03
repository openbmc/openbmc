<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:d="http://docbook.org/ns/docbook"
  xmlns="http://www.w3.org/1999/xhtml">

  <xsl:template match="glossentry/glossterm">
    <xsl:apply-imports/>
    <xsl:if test="$generate.permalink != 0">
      <xsl:call-template name="permalink">
        <xsl:with-param name="node" select=".."/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
