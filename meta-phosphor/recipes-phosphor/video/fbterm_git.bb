HOMEPAGE = "https://github.com/jk-ozlabs/fbterm"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=d8e20eece214df8ef953ed5857862150"
DEPENDS += "freetype"
DEPENDS += "fontconfig"
SRCREV = "c15430560aeb82a27358cc320af4a29e1296e6c1"
PV = "1.7+git${SRCPV}"
PR = "r1"

SRC_URI += "git://github.com/jk-ozlabs/fbterm.git;branch=master;protocol=https"
SRC_URI += "file://fb.modes"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "fbterm.service"
SYSTEMD_ENVIRONMENT_FILE:${PN} += "fbterm"

inherit autotools
inherit pkgconfig
inherit obmc-phosphor-systemd

do_install() {
    oe_runmake 'DESTDIR=${D}' install-exec
    install -d ${D}${sysconfdir}/
    install -m 0644 ${UNPACKDIR}/fb.modes ${D}${sysconfdir}/
}
