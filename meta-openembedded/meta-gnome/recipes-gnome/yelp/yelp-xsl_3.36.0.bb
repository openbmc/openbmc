SUMMARY = "XSL stylesheets for the yelp help browser"
LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=3e2bad3c5e3990988f9fa1bc5785b147 \
    file://COPYING.GPL;md5=eb723b61539feef013de476e68b5c50a \
    file://COPYING.LGPL;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

inherit gnomebase gettext itstool

DEPENDS += "libxml2"

SRC_URI[archive.md5sum] = "7d71af68fff4a92bcb2b8989f126be6c"
SRC_URI[archive.sha256sum] = "4fe51c0233b79a4c204c68498d45f09b342c30ed02c4e418506c0e35f0904ec3"

RDEPENDS_${PN}_append_class-target = " libxml2 itstool"

# ensure our native consumers are forced to inherit itstool
RDEPENDS_${PN}_append_class-native = " libxml2-native"

BBCLASSEXTEND = "native"
