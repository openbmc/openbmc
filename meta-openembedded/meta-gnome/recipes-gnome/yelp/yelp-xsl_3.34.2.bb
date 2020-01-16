SUMMARY = "XSL stylesheets for the yelp help browser"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=3e2bad3c5e3990988f9fa1bc5785b147 \
    file://COPYING.GPL;md5=eb723b61539feef013de476e68b5c50a \
    file://COPYING.LGPL;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

inherit gnomebase gettext itstool

DEPENDS += "libxml2"

SRC_URI[archive.md5sum] = "b9c1c53a9114b42054789f212ab37f59"
SRC_URI[archive.sha256sum] = "0c3fe6146113df26fb1295901b1c7baed9f0fe67a87f4345e11543aefe7cb7ad"

RDEPENDS_${PN}_append_class-target = " libxml2 itstool"

# ensure our native consumers are forced to inherit itstool
RDEPENDS_${PN}_append_class-native = " libxml2"

BBCLASSEXTEND = "native"
