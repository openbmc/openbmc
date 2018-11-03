SUMMARY = "Phosphor fan definition example data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-fan

PROVIDES += "virtual/phosphor-fan-control-fan-config"

SRC_URI += "file://fans.yaml"

S = "${WORKDIR}"

do_install() {
    DEST=${D}${control_datadir}
    install -D fans.yaml ${DEST}/fans.yaml
}
