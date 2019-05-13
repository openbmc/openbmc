SUMMARY = "Phosphor fan definition example data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit phosphor-fan

S = "${WORKDIR}"
PROVIDES += "virtual/phosphor-fan-control-fan-config"

SRC_URI = "file://fans.yaml"

do_install() {
    install -D fans.yaml ${D}${control_datadir}/fans.yaml
}

FILES_${PN} += "${control_datadir}/fans.yaml"
