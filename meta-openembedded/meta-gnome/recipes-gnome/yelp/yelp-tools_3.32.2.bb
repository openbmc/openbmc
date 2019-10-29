SUMMARY = "Collection of scripts and build utilities for documentation"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=d67c6f9f1515506abfea4f0d920c0774 \
    file://COPYING.GPL;md5=eb723b61539feef013de476e68b5c50a \
"

inherit gnomebase itstool

DEPENDS += " \
    libxslt-native \
    libxml2-native \
    yelp-xsl \
"

SRC_URI[archive.md5sum] = "7856f9ad0492aaf9adf097f5058bfc2e"
SRC_URI[archive.sha256sum] = "183856b5ed0b0bb2c05dd1204af023946ed436943e35e789afb0295e5e71e8f9"

RDEPENDS_${PN} += "yelp-xsl"

BBCLASSEXTEND = "native"
