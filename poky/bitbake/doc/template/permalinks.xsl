<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="generate.permalink" select="1"/>
  <xsl:param name="permalink.text">¶</xsl:param>

  <xsl:template name="permalink">
    <xsl:param name="node"/>

    <xsl:if test="$generate.permalink != '0'">
      <span class="permalink">
        <a alt="Permalink" title="Permalink">
          <xsl:attribute name="href">
            <xsl:call-template name="href.target">
              <xsl:with-param name="object"  select="$node"/>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:copy-of select="$permalink.text"/>
        </a>
      </span>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
