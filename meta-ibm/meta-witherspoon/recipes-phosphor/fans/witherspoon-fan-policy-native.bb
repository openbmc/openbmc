SUMMARY = "Fan policy for Witherspoon"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-dbus-monitor

SRC_URI += "file://air-cooled.yaml"
SRC_URI += "file://water-cooled.yaml"
SRC_URI += "file://fan-errors.yaml"

do_install() {
        install -D ${WORKDIR}/air-cooled.yaml ${D}${config_dir}/air-cooled.yaml
        install -D ${WORKDIR}/water-cooled.yaml ${D}${config_dir}/water-cooled.yaml
        install -D ${WORKDIR}/fan-errors.yaml ${D}${config_dir}/fan-errors.yaml
}
