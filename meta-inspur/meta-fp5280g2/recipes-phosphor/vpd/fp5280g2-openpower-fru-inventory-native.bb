SUMMARY = "Inspur FP5280G2 Inventory config for openpower-vpd-parser"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${INSPURBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit openpower-fru-vpd

SRC_URI += "file://inventory"

PROVIDES += "virtual/openpower-fru-inventory"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${inventory_datadir_native}
        install -d ${DEST}
        install inventory ${DEST}
}
