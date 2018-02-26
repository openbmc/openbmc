SUMMARY = "Linux zram compressed in-memory swap"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit update-rc.d systemd

RDEPENDS_${PN} = "util-linux-swaponoff kmod"
RRECOMMENDS_${PN} = "kernel-module-zram"

PR = "r3"

SRC_URI = " \
           file://init \
           file://zram.service \
"

do_install () {
    # Sysvinit
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/zram

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/zram.service ${D}${systemd_unitdir}/system
}

FILES_${PN} = "${sysconfdir}"
INITSCRIPT_NAME = "zram"
INITSCRIPT_PARAMS = "start 05 2 3 4 5 . stop 22 0 1 6 ."

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "zram.service"
