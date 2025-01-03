FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

SRC_URI += "file://clean-up-filesystem \
            file://clean-up-filesystem.service \
            "
CPU_CRIT_TGT = ""
STORAGE_CRIT_TGT = "clean-up-filesystem.service"

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += " \
    clean-up-filesystem.service \
    "
SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
    install -d ${D}${datadir}/phosphor-health-monitor
    install -m 0644 ${UNPACKDIR}/clean-up-filesystem.service ${D}${systemd_system_unitdir}/clean-up-filesystem.service
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/clean-up-filesystem ${D}${libexecdir}/${PN}/
}
