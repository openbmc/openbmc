SUMMARY = "Slot fault LED configurations for meta-yosemite4 machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
    file://slot-1.yaml \
    file://slot-2.yaml \
    file://slot-3.yaml \
    file://slot-4.yaml \
    file://slot-5.yaml \
    file://slot-6.yaml \
    file://slot-7.yaml \
    file://slot-8.yaml \
"

do_install() {
    install -D ${UNPACKDIR}/slot-1.yaml ${D}${config_dir}/slot-1.yaml
    install -D ${UNPACKDIR}/slot-2.yaml ${D}${config_dir}/slot-2.yaml
    install -D ${UNPACKDIR}/slot-3.yaml ${D}${config_dir}/slot-3.yaml
    install -D ${UNPACKDIR}/slot-4.yaml ${D}${config_dir}/slot-4.yaml
    install -D ${UNPACKDIR}/slot-5.yaml ${D}${config_dir}/slot-5.yaml
    install -D ${UNPACKDIR}/slot-6.yaml ${D}${config_dir}/slot-6.yaml
    install -D ${UNPACKDIR}/slot-7.yaml ${D}${config_dir}/slot-7.yaml
    install -D ${UNPACKDIR}/slot-8.yaml ${D}${config_dir}/slot-8.yaml
}

FILES:${PN}:append = " \
    ${config_dir}/slot-1.yaml \
    ${config_dir}/slot-2.yaml \
    ${config_dir}/slot-3.yaml \
    ${config_dir}/slot-4.yaml \
    ${config_dir}/slot-5.yaml \
    ${config_dir}/slot-6.yaml \
    ${config_dir}/slot-7.yaml \
    ${config_dir}/slot-8.yaml \
"

