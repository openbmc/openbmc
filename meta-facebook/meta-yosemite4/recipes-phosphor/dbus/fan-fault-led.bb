SUMMARY = "Fan fault led configurations for meta-yosemite4 machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
    file://board-0-fan-0.yaml \
    file://board-0-fan-1.yaml \
    file://board-0-fan-4.yaml \
    file://board-0-fan-5.yaml \
    file://board-0-fan-8.yaml \
    file://board-0-fan-9.yaml \
    file://board-1-fan-10.yaml \
    file://board-1-fan-11.yaml \
    file://board-1-fan-2.yaml \
    file://board-1-fan-3.yaml \
    file://board-1-fan-6.yaml \
    file://board-1-fan-7.yaml \
"

do_install() {
    install -D ${UNPACKDIR}/board-0-fan-0.yaml ${D}${config_dir}/board-0-fan-0.yaml
    install -D ${UNPACKDIR}/board-0-fan-1.yaml ${D}${config_dir}/board-0-fan-1.yaml
    install -D ${UNPACKDIR}/board-0-fan-4.yaml ${D}${config_dir}/board-0-fan-4.yaml
    install -D ${UNPACKDIR}/board-0-fan-5.yaml ${D}${config_dir}/board-0-fan-5.yaml
    install -D ${UNPACKDIR}/board-0-fan-8.yaml ${D}${config_dir}/board-0-fan-8.yaml
    install -D ${UNPACKDIR}/board-0-fan-9.yaml ${D}${config_dir}/board-0-fan-9.yaml
    install -D ${UNPACKDIR}/board-1-fan-10.yaml ${D}${config_dir}/board-1-fan-10.yaml
    install -D ${UNPACKDIR}/board-1-fan-11.yaml ${D}${config_dir}/board-1-fan-11.yaml
    install -D ${UNPACKDIR}/board-1-fan-2.yaml ${D}${config_dir}/board-1-fan-2.yaml
    install -D ${UNPACKDIR}/board-1-fan-3.yaml ${D}${config_dir}/board-1-fan-3.yaml
    install -D ${UNPACKDIR}/board-1-fan-6.yaml ${D}${config_dir}/board-1-fan-6.yaml
    install -D ${UNPACKDIR}/board-1-fan-7.yaml ${D}${config_dir}/board-1-fan-7.yaml
}

FILES:${PN}:append = " \
    ${config_dir}/board-0-fan-0.yaml \
    ${config_dir}/board-0-fan-1.yaml \
    ${config_dir}/board-0-fan-4.yaml \
    ${config_dir}/board-0-fan-5.yaml \
    ${config_dir}/board-0-fan-8.yaml \
    ${config_dir}/board-0-fan-9.yaml \
    ${config_dir}/board-1-fan-10.yaml \
    ${config_dir}/board-1-fan-11.yaml \
    ${config_dir}/board-1-fan-2.yaml \
    ${config_dir}/board-1-fan-3.yaml \
    ${config_dir}/board-1-fan-6.yaml \
    ${config_dir}/board-1-fan-7.yaml \
"

