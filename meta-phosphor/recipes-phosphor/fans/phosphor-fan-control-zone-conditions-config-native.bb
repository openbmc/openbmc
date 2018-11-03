SUMMARY = "Phosphor zone conditions definition default data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-fan

SRC_URI += "file://zone_conditions.yaml"

S = "${WORKDIR}"

do_install() {
    DEST=${D}${control_datadir}
    install -D zone_conditions.yaml ${DEST}/zone_conditions.yaml
}
