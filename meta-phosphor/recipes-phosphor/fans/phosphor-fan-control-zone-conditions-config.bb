SUMMARY = "Phosphor zone conditions definition default data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit phosphor-fan

S = "${WORKDIR}"

SRC_URI = "file://zone_conditions.yaml"

do_install() {
    install -D zone_conditions.yaml ${D}${control_datadir}/zone_conditions.yaml
}

FILES_${PN} += "${control_datadir}/zone_conditions.yaml"
