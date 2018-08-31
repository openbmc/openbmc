HOMEPAGE = "https://github.com/jk-ozlabs/fbterm"
LICENSE = "GPLv2+"

SRC_URI += "git://github.com/jk-ozlabs/fbterm.git;nobranch=1"
SRC_URI += "file://fb.modes"
PR = "r1"

LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=d8e20eece214df8ef953ed5857862150"

DEPENDS += "freetype"
DEPENDS += "fontconfig"

inherit autotools
inherit pkgconfig
inherit obmc-phosphor-systemd

SRCREV = "c15430560aeb82a27358cc320af4a29e1296e6c1"
PV = "1.7+git${SRCPV}"

S = "${WORKDIR}/git"

do_install() {
    oe_runmake 'DESTDIR=${D}' install-exec
    install -d ${D}${sysconfdir}/
    install -m 0644 ${WORKDIR}/fb.modes ${D}${sysconfdir}/
}

SYSTEMD_SERVICE_${PN} += "fbterm.service"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "fbterm"
