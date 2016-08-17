SUMMARY = "Simple Xserver Init Script (no dm)"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
SECTION = "x11"

PR = "r22"

SRC_URI = "file://xserver-nodm \
           file://gplv2-license.patch \
           file://xserver-nodm.service \
           file://xserver-nodm.conf \
"
S = "${WORKDIR}"

inherit update-rc.d systemd

INITSCRIPT_NAME = "xserver-nodm"
INITSCRIPT_PARAMS = "start 01 5 . stop 01 0 1 2 3 6 ."
INITSCRIPT_PARAMS_shr = "start 90 5 . stop 90 0 1 2 3 6 ."

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install xserver-nodm ${D}${sysconfdir}/init.d

    install -d ${D}${sysconfdir}/default

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install xserver-nodm.conf ${D}${sysconfdir}/default/xserver-nodm
        install -m 0644 ${WORKDIR}/xserver-nodm.service ${D}${systemd_unitdir}/system
    fi
}

RDEPENDS_${PN} = "xserver-common (>= 1.30) xinit"

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "xserver-nodm.service"

FILES_${PN} += "${sysconfdir}/default/xserver-nodm"
