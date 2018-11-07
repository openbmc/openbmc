SUMMARY = "Inventory to Sensor config for Romulus"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-ipmi-host

PROVIDES += "virtual/phosphor-ipmi-inventory-sel"

SRC_URI += "file://sel-config.yaml"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${sensor_datadir}
        install -d ${DEST}
        install sel-config.yaml ${DEST}/invsensor.yaml
}
