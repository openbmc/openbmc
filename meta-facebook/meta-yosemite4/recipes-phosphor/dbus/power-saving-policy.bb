SUMMARY = "Power saving policy configurations for meta-yosemite4 machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
    file://nic-1.yaml \
    file://nic-2.yaml \
    file://nic-3.yaml \
    file://nic-4.yaml \
"

do_install() {
    install -D ${UNPACKDIR}/nic-1.yaml ${D}${config_dir}/nic-1.yaml
    install -D ${UNPACKDIR}/nic-2.yaml ${D}${config_dir}/nic-2.yaml
    install -D ${UNPACKDIR}/nic-3.yaml ${D}${config_dir}/nic-3.yaml
    install -D ${UNPACKDIR}/nic-4.yaml ${D}${config_dir}/nic-4.yaml
}

FILES:${PN}:append = " \
    ${config_dir}/nic-1.yaml \
    ${config_dir}/nic-2.yaml \
    ${config_dir}/nic-3.yaml \
    ${config_dir}/nic-4.yaml \
"

