<!--
This XSL sheet enables creation of permalinks for <para><code>
constructs.  Right now, this construct occurs only in the ref-manual
book's qa issues and warnings chapter.  However, if the construct
were to appear anywhere in that ref-manual, a permalink would be
generated.  I don't foresee any <para><code> constructs being used
in the future but if they are then a permalink with a generically
numbered permalink would be generated.
-->
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:d="http://docbook.org/ns/docbook"
  xmlns="http://www.w3.org/1999/xhtml">

  <xsl:template match="para/code">
    <xsl:apply-imports/>
    <xsl:if test="$generate.permalink != 0">
      <xsl:call-template name="permalink">
        <xsl:with-param name="node" select=".."/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
