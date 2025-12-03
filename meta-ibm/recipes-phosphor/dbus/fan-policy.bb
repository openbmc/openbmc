SUMMARY = "Fan policy configurations for meta-witherspoon machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:df-openpower = " file://air-cooled.yaml"
SRC_URI:append:ibm-ac-server = " file://water-cooled.yaml"
SRC_URI:append:df-openpower = " file://fan-errors.yaml"

SRC_URI:remove:ibm-enterprise = "file://air-cooled.yaml"
SRC_URI:remove:ibm-enterprise = "file://fan-errors.yaml"

do_install:append:ibm-ac-server() {
        install -D ${UNPACKDIR}/air-cooled.yaml ${D}${config_dir}/air-cooled.yaml
        install -D ${UNPACKDIR}/water-cooled.yaml ${D}${config_dir}/water-cooled.yaml
        install -D ${UNPACKDIR}/fan-errors.yaml ${D}${config_dir}/fan-errors.yaml
}


FILES:${PN} += "${config_dir}/air-cooled.yaml"
FILES:${PN}:append:ibm-ac-server = " ${config_dir}/water-cooled.yaml"
FILES:${PN} += "${config_dir}/fan-errors.yaml"

FILES:${PN}:remove:ibm-enterprise = "${config_dir}/air-cooled.yaml"
FILES:${PN}:remove:ibm-enterprise = "${config_dir}/fan-errors.yaml"
