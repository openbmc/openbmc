SUMMARY = "XSL stylesheets for the yelp help browser"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=3e2bad3c5e3990988f9fa1bc5785b147 \
    file://COPYING.GPL;md5=eb723b61539feef013de476e68b5c50a \
    file://COPYING.LGPL;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

inherit gnomebase gettext itstool

DEPENDS += "libxml2"

SRC_URI[archive.md5sum] = "f8c4e777aee8b055251c333ef48a0cd0"
SRC_URI[archive.sha256sum] = "e8063aee67d1df634f3d062f1c28130b2dabb3c0c66396b1af90388f34e14ee2"

RDEPENDS_${PN}_append_class-target = " libxml2 itstool"

# ensure our native consumers are forced to inherit itstool
RDEPENDS_${PN}_append_class-native = " libxml2"

BBCLASSEXTEND = "native"
