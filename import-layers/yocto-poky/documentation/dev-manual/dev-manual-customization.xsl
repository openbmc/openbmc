<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">

  <xsl:import href="http://downloads.yoctoproject.org/mirror/docbook-mirror/docbook-xsl-1.76.1/xhtml/docbook.xsl" />

<!--

  <xsl:import href="../template/1.76.1/docbook-xsl-1.76.1/xhtml/docbook.xsl" />

  <xsl:import href="http://docbook.sourceforge.net/release/xsl/1.76.1/xhtml/docbook.xsl" />

-->

  <xsl:include href="../template/permalinks.xsl"/>
  <xsl:include href="../template/section.title.xsl"/>
  <xsl:include href="../template/component.title.xsl"/>
  <xsl:include href="../template/division.title.xsl"/>
  <xsl:include href="../template/formal.object.heading.xsl"/>

  <xsl:param name="html.stylesheet" select="'dev-style.css'" />
  <xsl:param name="chapter.autolabel" select="1" />
  <xsl:param name="appendix.autolabel" select="A" />
  <xsl:param name="section.autolabel" select="1" />
  <xsl:param name="section.label.includes.component.label" select="1" />
  <xsl:param name="generate.id.attributes" select="1" />

</xsl:stylesheet>
