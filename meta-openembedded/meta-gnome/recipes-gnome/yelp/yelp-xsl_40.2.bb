SUMMARY = "XSL stylesheets for the yelp help browser"
LICENSE = "LGPLv2.1 & GPLv2 & BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=8ca13a5a6972ac1620a1e42a3dacd774 \
    file://COPYING.GPL;md5=eb723b61539feef013de476e68b5c50a \
    file://COPYING.LGPL;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

inherit gnomebase gettext itstool

DEPENDS += "libxml2"

SRC_URI[archive.sha256sum] = "919c552f846aaec741fe51d5af416b469bcdb25c77effc3b5a15a4b4b7ff814e"

RDEPENDS:${PN}:append:class-target = " libxml2 itstool"

# ensure our native consumers are forced to inherit itstool
RDEPENDS:${PN}:append:class-native = " libxml2-native"

BBCLASSEXTEND = "native"
