SUMMARY = "Phosphor fan zone definition default data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit phosphor-fan

S = "${WORKDIR}"

SRC_URI = "file://zones.yaml"

do_install() {
    install -D zones.yaml ${D}${control_datadir}/zones.yaml
}

FILES_${PN} += "${control_datadir}/zones.yaml"
