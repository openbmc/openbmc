SUMMARY = "Thermal policy configuration for Yosemite5 platforms"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
    file://thermal-unr.yaml \
    file://thermal-unr-policy \
    file://thermal-unr-policy.service \
"

RDEPENDS:${PN} += "bash"

do_install() {
    install -d ${D}${config_dir}
    install -m 0644 ${UNPACKDIR}/*.yaml ${D}${config_dir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/

    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${UNPACKDIR}/thermal-unr-policy ${D}${libexecdir}/${PN}/
}

FILES:${PN}:append = " \
    ${config_dir}/*.yaml \
    ${systemd_system_unitdir}/*.service \
"