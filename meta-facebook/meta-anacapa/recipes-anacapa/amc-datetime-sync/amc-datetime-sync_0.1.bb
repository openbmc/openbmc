SUMMARY = "AMC Datetime Sync"
DESCRIPTION = "AMC Datetime Sync"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${UNPACKDIR}"
SRC_URI += " \
    file://amc-datetime-sync \
    file://amc-datetime-sync.service \
    file://amc-datetime-sync.timer \
    "

RDEPENDS:${PN} += " bash"

inherit systemd

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "amc-datetime-sync.timer"

do_install() {
    install -d ${D}${libexecdir}/${BPN}
    install -m 0755 ${UNPACKDIR}/amc-datetime-sync ${D}${libexecdir}/${BPN}/amc-datetime-sync

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/amc-datetime-sync.service ${D}${systemd_system_unitdir}/amc-datetime-sync.service
    install -m 0644 ${UNPACKDIR}/amc-datetime-sync.timer ${D}${systemd_system_unitdir}/amc-datetime-sync.timer
}

FILES:${PN} += "${systemd_system_unitdir}"
