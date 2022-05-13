SUMMARY = "Iptable Restore"
DESCRIPTION = "Restore iptable rules"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd

SRC_URI = "file://phosphor-restore-iptable-rules.service"
SYSTEMD_SERVICE:${PN} = "phosphor-restore-iptable-rules.service"

S = "${WORKDIR}"
do_install() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/phosphor-restore-iptable-rules.service \
        ${D}${systemd_unitdir}/system
}
