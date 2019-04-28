SUMMARY = "Romulus OCC Control sensor IDs"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit openpower-occ-control

SRC_URI += "file://occ_sensor.yaml"

PROVIDES += "virtual/openpower-occ-control-config-native"

S = "${WORKDIR}"

do_install() {
    install -d ${YAML_DEST}/
    install -D occ_sensor.yaml ${YAML_DEST}/occ_sensor.yaml
}
