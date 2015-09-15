<?xml version='1.0'?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	version="1.0">

  <xsl:import href="http://downloads.yoctoproject.org/mirror/docbook-mirror/docbook-xsl-1.76.1/eclipse/eclipse3.xsl" />

<!--

  <xsl:import href="../template/1.76.1/docbook-xsl-1.76.1/eclipse/eclipse3.xsl" />

  <xsl:import
	  href="http://docbook.sourceforge.net/release/xsl/1.76.1/eclipse/eclipse3.xsl" />

-->

  <xsl:param name="chunker.output.indent" select="'yes'"/>
  <xsl:param name="chunk.quietly" select="1"/>
  <xsl:param name="chunk.first.sections" select="1"/>
  <xsl:param name="chunk.section.depth" select="10"/>
  <xsl:param name="use.id.as.filename" select="1"/>
  <xsl:param name="ulink.target" select="'_self'" />
  <xsl:param name="base.dir" select="'html/bsp-guide/'"/>
  <xsl:param name="html.stylesheet" select="'../book.css'"/>
  <xsl:param name="eclipse.manifest" select="0"/>
  <xsl:param name="create.plugin.xml" select="0"/>
  <xsl:param name="suppress.navigation" select="1"/>
  <xsl:param name="generate.index" select="0"/>
  <xsl:param name="chapter.autolabel" select="1" />
  <xsl:param name="appendix.autolabel" select="1" />
  <xsl:param name="section.autolabel" select="1" />
  <xsl:param name="section.label.includes.component.label" select="1" />
</xsl:stylesheet>
