SUMMARY = "Thermal policy configuration for meta-witherspoon machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI = "file://thermal-policy.yaml"

do_install() {
        install -D ${WORKDIR}/thermal-policy.yaml ${D}${config_dir}/thermal-policy.yaml
}

FILES_${PN} += "${config_dir}/thermal-policy.yaml"
