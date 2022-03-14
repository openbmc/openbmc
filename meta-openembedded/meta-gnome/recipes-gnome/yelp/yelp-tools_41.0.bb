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

SRC_URI[archive.sha256sum] = "37f1acc02bcbe68a31b86e07c129a839bd3276e656dc89eb7fc0a92746eff272"

RDEPENDS:${PN} += "python3-core yelp-xsl"

BBCLASSEXTEND = "native"
