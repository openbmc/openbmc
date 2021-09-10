SUMMARY = "Collection of scripts and build utilities for documentation"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=d67c6f9f1515506abfea4f0d920c0774 \
    file://COPYING.GPL;md5=eb723b61539feef013de476e68b5c50a \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase itstool

DEPENDS += " \
    libxslt-native \
    libxml2-native \
    python3-lxml-native \
    yelp-xsl \
"

SRC_URI[archive.sha256sum] = "664bacf2f3dd65ef00a43f79487351ab64a6c4c629c56ac0ceb1723c2eb66aae"

RDEPENDS:${PN} += "python3-core yelp-xsl"

BBCLASSEXTEND = "native"
