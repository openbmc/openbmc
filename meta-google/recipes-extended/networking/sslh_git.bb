PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/yrutschle/sslh"
SRCREV = "b72baa0622ae2d29152dc251d7c21fe80a3052da"
S = "${WORKDIR}/git"

inherit perlnative

DEPENDS += "conf2struct-native"
DEPENDS += "libbsd"
DEPENDS += "libcap"
DEPENDS += "libconfig"
DEPENDS += "systemd"
DEPENDS += "pcre"

EXTRA_OEMAKE += "DESTDIR=${D}"
EXTRA_OEMAKE += "PREFIX=${prefix}"
EXTRA_OEMAKE += "USELIBCAP=1"
EXTRA_OEMAKE += "USELIBBSD=1"
EXTRA_OEMAKE += "USESYSTEMD=1"

do_compile() {
    oe_runmake
}

do_install() {
    oe_runmake install
}
