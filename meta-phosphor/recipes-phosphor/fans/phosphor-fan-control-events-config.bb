SUMMARY = "Phosphor zone events definition default data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit phosphor-fan

S = "${WORKDIR}"

SRC_URI = "file://events.yaml"

do_install() {
    install -D events.yaml ${D}${control_datadir}/events.yaml
}

FILES_${PN} += "${control_datadir}/events.yaml"
