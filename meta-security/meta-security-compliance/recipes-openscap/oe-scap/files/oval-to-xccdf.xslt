<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright 2012 Red Hat Inc., Durham, North Carolina. All Rights Reserved.

This transformation is free software; you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation; either version 2.1 of the License.

This transformation is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
for more details.

You should have received a copy of the GNU Lesser General Public License along
with this library; if not, write to the Free Software Foundation, Inc., 59
Temple Place, Suite 330, Boston, MA  02111-1307 USA

Authors:
     Šimon Lukašík <slukasik@redhat.com>
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xccdf="http://checklists.nist.gov/xccdf/1.1"
    xmlns:oval="http://oval.mitre.org/XMLSchema/oval-common-5"
    xmlns:oval-def="http://oval.mitre.org/XMLSchema/oval-definitions-5">
    <xsl:output method="xml" encoding="UTF-8"/>

    <xsl:template match="/">
        <xccdf:Benchmark id="generated-xccdf" resolved="1">
            <xccdf:status>incomplete</xccdf:status>
            <xccdf:title>
                <xsl:text>Automatically generated XCCDF from OVAL file: </xsl:text>
                <xsl:value-of select="$ovalfile"/>
            </xccdf:title>
            <xccdf:description>This file has been generated automatically from oval definitions file.</xccdf:description>
            <xccdf:version>
                <xsl:attribute name="time">
                    <xsl:value-of select="normalize-space(oval-def:oval_definitions/oval-def:generator/oval:timestamp[1]/text())"/>
                </xsl:attribute>
                <xsl:text>None, generated from OVAL file.</xsl:text>
            </xccdf:version>
            <xsl:apply-templates select="oval-def:oval_definitions/oval-def:definitions/oval-def:definition"/>
        </xccdf:Benchmark>
    </xsl:template>

    <xsl:template match="oval-def:definition">
        <xccdf:Rule selected="true">
            <xsl:attribute name="id">
                <xsl:value-of select="translate(@id,':','-')"/>
            </xsl:attribute>
            <xccdf:title>
                <xsl:copy-of select="oval-def:metadata/oval-def:title/text()"/>
            </xccdf:title>
            <xsl:apply-templates select="oval-def:metadata/oval-def:advisory/oval-def:cve"/>
                <xccdf:check system="http://oval.mitre.org/XMLSchema/oval-definitions-5">
                    <xccdf:check-content-ref href="file">
                        <xsl:attribute name="name">
                            <xsl:value-of select="@id"/>
                        </xsl:attribute>
                        <xsl:attribute name="href">
                            <xsl:value-of select="$ovalfile"/>
                        </xsl:attribute>
                    </xccdf:check-content-ref>
                </xccdf:check>
        </xccdf:Rule>
    </xsl:template>

    <xsl:template match="oval-def:cve">
        <xccdf:ident system="http://cve.mitre.org">
            <xsl:copy-of select="text()"/>
        </xccdf:ident>
    </xsl:template>
</xsl:stylesheet>

