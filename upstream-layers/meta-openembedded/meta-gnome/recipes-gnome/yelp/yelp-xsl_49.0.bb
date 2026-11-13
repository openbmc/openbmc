SUMMARY = "XSL stylesheets for the yelp help browser"
LICENSE = "BSD-3-Clause AND GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=8ca13a5a6972ac1620a1e42a3dacd774 \
    file://COPYING.GPL;md5=eb723b61539feef013de476e68b5c50a \
    file://COPYING.LGPL;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

inherit gnomebase gettext itstool

DEPENDS += "libxml2 libxslt-native"
DEPENDS:append:class-native = " gettext-native"

SRC_URI[archive.sha256sum] = "59d43a8f8fe67b784f14f9a04dd4a7a092a7f4a64a65e71b90fe02a47a50fbec"

RDEPENDS:${PN}:append:class-target = " libxml2 itstool"

# ensure our native consumers are forced to inherit itstool
RDEPENDS:${PN}:append:class-native = " libxml2-native"

BBCLASSEXTEND = "native"
