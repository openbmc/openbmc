<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:d="http://docbook.org/ns/docbook">

   <xsl:output method="html" />

   <xsl:template match="/d:chapter/d:section/d:mediaobject">
      <xsl:for-each select=".">
         <xsl:variable name="vid_url">
            <xsl:value-of select="./d:videoobject/d:videodata/@fileref" />
         </xsl:variable>
         <div style="text-align: center; margin: auto">
            <object type="application/x-shockwave-flash" width="640" height="420" data="{$vid_url}?color2=FBE9EC&amp;showsearch=0&amp;version=3&amp;modestbranding=1&amp;fs=1">
               <param name="movie" value="{$vid_url}?color2=FBE9EC&amp;showsearch=0&amp;version=3&amp;modestbranding=1&amp;fs=1" />
               <param name="allowFullScreen" value="true" />
               <param name="allowscriptaccess" value="always" />
            </object>
         </div>
      </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
