SUMMARY = "Dummy image uploader for sending debug binaries"
DESCRIPTION = "Dummy image uploader for sending debug binaries"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI += "file://config-dummy.json"
SRC_URI += "file://dummy-verify.service"

FILES:${PN} += "${datadir}/phosphor-ipmi-flash"

SYSTEMD_SERVICE:${PN} += "dummy-verify.service"

do_install() {
    install -d ${D}${datadir}/phosphor-ipmi-flash
    install -m 0644 ${UNPACKDIR}/config-dummy.json ${D}${datadir}/phosphor-ipmi-flash

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/dummy-verify.service ${D}${systemd_system_unitdir}
}
