SUMMARY = "Debug collector error watch config file"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native

SRC_URI += "file://errors_watch.yaml"

PROVIDES += "virtual/phosphor-debug-errors"

S = "${WORKDIR}"

do_install_append() {
    DEST=${D}${datadir}/dump
    install -d ${DEST}
    install errors_watch.yaml ${DEST}/
}

