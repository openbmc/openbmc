SUMMARY = "Romulus inventory map for phosphor-ipmi-host"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-logging

SRC_URI += "file://callouts.yaml"

PROVIDES += "virtual/phosphor-logging-callouts"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${callouts_datadir}
        install -d ${DEST}
        install callouts.yaml ${DEST}
}

