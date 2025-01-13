SUMMARY = "YAML configuration for IBM SBP1"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

RDEPENDS:${PN} += "bash flashrom"

SRC_URI += " \
    file://bios-version.sh \
    file://bios-version.service \
    "

do_install:append() {
    install -d ${D}/${sbindir}
    install -m 0755 ${UNPACKDIR}/bios-version.sh ${D}/${sbindir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/bios-version.service ${D}${systemd_system_unitdir}/
}

SYSTEMD_SERVICE:${PN} += "bios-version.service"

FILES:${PN} += "${systemd_system_unitdir}/bios-version.service ${sbindir}/bios-version.sh"

