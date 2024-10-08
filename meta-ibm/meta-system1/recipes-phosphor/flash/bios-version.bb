SUMMARY = "YAML configuration for IBM System1"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

RDEPENDS:${PN} += "bash flashrom"

SRC_URI += " \
    file://bios-version.sh \
    file://bios-version.service \
    file://pch-standby.service \
    file://pch-standby-check.sh \
    "

do_install:append() {
    install -d ${D}${libexecdir}
    install -m 0755 ${WORKDIR}/bios-version.sh ${D}${libexecdir}/
    install -m 0755 ${WORKDIR}/pch-standby-check.sh ${D}${libexecdir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/bios-version.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/pch-standby.service ${D}${systemd_system_unitdir}/
}

SYSTEMD_SERVICE:${PN} += "bios-version.service"
SYSTEMD_SERVICE:${PN} += "pch-standby.service"

FILES:${PN} += "${systemd_system_unitdir}/bios-version.service ${libexecdir}/bios-version.sh"
FILES:${PN} += "${systemd_system_unitdir}/pch-standby.service ${libexecdir}/pch-standby-check.sh"

