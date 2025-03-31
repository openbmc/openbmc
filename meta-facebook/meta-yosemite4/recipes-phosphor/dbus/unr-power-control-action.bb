SUMMARY = "Sensors alarm action configurations for meta-yosemite4 machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " \
    file://unr-action-nic0-poweroff.yaml \
    file://unr-action-nic1-poweroff.yaml \
    file://unr-action-nic2-poweroff.yaml \
    file://unr-action-nic3-poweroff.yaml \
    file://unr-action-slot1-temp-poweroff.yaml \
    file://unr-action-slot2-temp-poweroff.yaml \
    file://unr-action-slot3-temp-poweroff.yaml \
    file://unr-action-slot4-temp-poweroff.yaml \
    file://unr-action-slot5-temp-poweroff.yaml \
    file://unr-action-slot6-temp-poweroff.yaml \
    file://unr-action-slot7-temp-poweroff.yaml \
    file://unr-action-slot8-temp-poweroff.yaml \
"

do_install() {
    install -D ${UNPACKDIR}/unr-action-nic0-poweroff.yaml ${D}${config_dir}/unr-action-nic0-poweroff.yaml
    install -D ${UNPACKDIR}/unr-action-nic1-poweroff.yaml ${D}${config_dir}/unr-action-nic1-poweroff.yaml
    install -D ${UNPACKDIR}/unr-action-nic2-poweroff.yaml ${D}${config_dir}/unr-action-nic2-poweroff.yaml
    install -D ${UNPACKDIR}/unr-action-nic3-poweroff.yaml ${D}${config_dir}/unr-action-nic3-poweroff.yaml
    install -D ${UNPACKDIR}/unr-action-slot1-temp-poweroff.yaml ${D}${config_dir}/unr-action-slot1-temp-poweroff.yaml
    install -D ${UNPACKDIR}/unr-action-slot2-temp-poweroff.yaml ${D}${config_dir}/unr-action-slot2-temp-poweroff.yaml
    install -D ${UNPACKDIR}/unr-action-slot3-temp-poweroff.yaml ${D}${config_dir}/unr-action-slot3-temp-poweroff.yaml
    install -D ${UNPACKDIR}/unr-action-slot4-temp-poweroff.yaml ${D}${config_dir}/unr-action-slot4-temp-poweroff.yaml
    install -D ${UNPACKDIR}/unr-action-slot5-temp-poweroff.yaml ${D}${config_dir}/unr-action-slot5-temp-poweroff.yaml
    install -D ${UNPACKDIR}/unr-action-slot6-temp-poweroff.yaml ${D}${config_dir}/unr-action-slot6-temp-poweroff.yaml
    install -D ${UNPACKDIR}/unr-action-slot7-temp-poweroff.yaml ${D}${config_dir}/unr-action-slot7-temp-poweroff.yaml
    install -D ${UNPACKDIR}/unr-action-slot8-temp-poweroff.yaml ${D}${config_dir}/unr-action-slot8-temp-poweroff.yaml
}

FILES:${PN}:append = " \
    ${config_dir}/unr-action-nic0-poweroff.yaml \
    ${config_dir}/unr-action-nic1-poweroff.yaml \
    ${config_dir}/unr-action-nic2-poweroff.yaml \
    ${config_dir}/unr-action-nic3-poweroff.yaml \
    ${config_dir}/unr-action-slot1-temp-poweroff.yaml \
    ${config_dir}/unr-action-slot2-temp-poweroff.yaml \
    ${config_dir}/unr-action-slot3-temp-poweroff.yaml \
    ${config_dir}/unr-action-slot4-temp-poweroff.yaml \
    ${config_dir}/unr-action-slot5-temp-poweroff.yaml \
    ${config_dir}/unr-action-slot6-temp-poweroff.yaml \
    ${config_dir}/unr-action-slot7-temp-poweroff.yaml \
    ${config_dir}/unr-action-slot8-temp-poweroff.yaml \
"

