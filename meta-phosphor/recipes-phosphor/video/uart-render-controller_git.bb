HOMEPAGE = "https://github.com/jk-ozlabs/uart-render-controller"
LICENSE = "GPLv2+"

SRC_URI += "git://github.com/jk-ozlabs/uart-render-controller;branch=master"
SRC_URI += "file://uart-render-controller.service"

PR = "r1"

LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools
inherit pkgconfig
inherit systemd

DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"
RDEPENDS_${PN} += "fbterm"

SRCREV = "26ac7f7bd6af52db63451d3633bcf1b167eea3d1"
PV = "0.1+git${SRCPV}"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} += "uart-render-controller.service"

do_install_append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/uart-render-controller.service ${D}${systemd_system_unitdir}/
}
