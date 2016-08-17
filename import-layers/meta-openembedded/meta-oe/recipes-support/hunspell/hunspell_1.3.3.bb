SUMMARY = "A spell checker and morphological analyzer library"
HOMEPAGE = "http://hunspell.sourceforge.net/"
LICENSE = "LGPLv2.1 | GPLv2 | MPLv1.1"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=ed3a37b3ba6d6be3e08ab45987cf1b88 \
    file://COPYING.LGPL;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
    file://COPYING.MPL;md5=bfe1f75d606912a4111c90743d6c7325 \
"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/Hunspell/${PV}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "4967da60b23413604c9e563beacc63b4"
SRC_URI[sha256sum] = "a7b2c0de0e2ce17426821dc1ac8eb115029959b3ada9d80a81739fa19373246c"

inherit autotools pkgconfig gettext

RDEPENDS_${PN} = "perl"

BBCLASSEXTEND = "native"
