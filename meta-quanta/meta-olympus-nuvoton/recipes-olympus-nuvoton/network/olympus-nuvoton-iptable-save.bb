SUMMARY = "Iptable Save"
DESCRIPTION = "Save iptable rules"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd

SRC_URI = "file://phosphor-save-iptable-rules.service"
SYSTEMD_SERVICE:${PN} = "phosphor-save-iptable-rules.service"

S = "${WORKDIR}"
do_install() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/phosphor-save-iptable-rules.service \
        ${D}${systemd_unitdir}/system
}
