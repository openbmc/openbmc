SUMMARY = "Fan policy configurations for meta-witherspoon machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "file://air-cooled.yaml"
SRC_URI_append_ibm-ac-server = " file://water-cooled.yaml"
SRC_URI += "file://fan-errors.yaml"

SRC_URI_remove_rainier = "file://air-cooled.yaml"
SRC_URI_remove_rainier = "file://fan-errors.yaml"

do_install_append_ibm-ac-server() {
        install -D ${WORKDIR}/air-cooled.yaml ${D}${config_dir}/air-cooled.yaml
        install -D ${WORKDIR}/water-cooled.yaml ${D}${config_dir}/water-cooled.yaml
        install -D ${WORKDIR}/fan-errors.yaml ${D}${config_dir}/fan-errors.yaml
}

do_install_append_mihawk() {
        install -D ${WORKDIR}/air-cooled.yaml ${D}${config_dir}/air-cooled.yaml
        install -D ${WORKDIR}/fan-errors.yaml ${D}${config_dir}/fan-errors.yaml
}

FILES_${PN} += "${config_dir}/air-cooled.yaml"
FILES_${PN}_append_ibm-ac-server = " ${config_dir}/water-cooled.yaml"
FILES_${PN} += "${config_dir}/fan-errors.yaml"

FILES_${PN}_remove_rainier = "${config_dir}/air-cooled.yaml"
FILES_${PN}_remove_rainier = "${config_dir}/fan-errors.yaml"
