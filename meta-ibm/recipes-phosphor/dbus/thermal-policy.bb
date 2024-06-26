SUMMARY = "Thermal policy configuration for meta-witherspoon machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:ibm-ac-server = " file://thermal-policy.yaml"

do_install:ibm-ac-server() {
        install -D ${UNPACKDIR}/thermal-policy.yaml ${D}${config_dir}/thermal-policy.yaml
}


FILES:${PN}:append:ibm-ac-server = " ${config_dir}/thermal-policy.yaml"