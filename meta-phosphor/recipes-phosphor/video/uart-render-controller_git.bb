HOMEPAGE = "https://github.com/jk-ozlabs/uart-render-controller"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"
DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"
SRCREV = "08e854a6c425011d029e4e02241afee5060f15eb"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/jk-ozlabs/uart-render-controller;branch=master;protocol=https"
SRC_URI += "file://uart-render-controller.service"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "uart-render-controller.service"

inherit autotools
inherit pkgconfig
inherit systemd

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/uart-render-controller.service ${D}${systemd_system_unitdir}/
}

RDEPENDS:${PN} += "fbterm"
