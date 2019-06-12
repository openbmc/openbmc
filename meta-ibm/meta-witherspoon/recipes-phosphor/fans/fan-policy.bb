SUMMARY = "Fan policy configurations for meta-witherspoon machines"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit phosphor-dbus-monitor

FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"

SRC_URI += "file://air-cooled.yaml"
SRC_URI += "file://water-cooled.yaml"
SRC_URI += "file://fan-errors.yaml"

do_install() {
        install -D ${WORKDIR}/air-cooled.yaml ${D}${config_dir}/air-cooled.yaml
        install -D ${WORKDIR}/water-cooled.yaml ${D}${config_dir}/water-cooled.yaml
        install -D ${WORKDIR}/fan-errors.yaml ${D}${config_dir}/fan-errors.yaml
}

FILES_${PN} += "${config_dir}/air-cooled.yaml"
FILES_${PN} += "${config_dir}/water-cooled.yaml"
FILES_${PN} += "${config_dir}/fan-errors.yaml"
