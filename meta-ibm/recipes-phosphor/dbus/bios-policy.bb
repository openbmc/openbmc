SUMMARY = "Bios policy configurations for sbp1 machine"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:sbp1 = " file://bios-policy.yaml"

do_install:append:sbp1() {
        install -D ${WORKDIR}/bios-policy.yaml ${D}${config_dir}/bios-policy.yaml
}

FILES:${PN} += "${config_dir}/bios-policy.yaml"
