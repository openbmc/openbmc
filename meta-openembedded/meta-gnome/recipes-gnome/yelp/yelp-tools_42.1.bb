SUMMARY = "Collection of scripts and build utilities for documentation"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=d67c6f9f1515506abfea4f0d920c0774 \
    file://COPYING.GPL;md5=eb723b61539feef013de476e68b5c50a \
"

inherit gnomebase itstool

DEPENDS += " \
    libxslt-native \
    libxml2-native \
    python3-lxml-native \
    yelp-xsl \
"

SRC_URI[archive.sha256sum] = "3e496a4020d4145b99fd508a25fa09336a503a4e8900028421e72c6a4b11f905"

RDEPENDS:${PN} += "python3-core yelp-xsl"

BBCLASSEXTEND = "native"
