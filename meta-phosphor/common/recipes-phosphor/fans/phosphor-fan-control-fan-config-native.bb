SUMMARY = "Phosphor fan definition example data"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-fan

PROVIDES += "virtual/phosphor-fan-control-fan-config"

SRC_URI += "file://fans.yaml"

S = "${WORKDIR}"

do_install() {
    DEST=${D}${control_datadir}
    install -D fans.yaml ${DEST}/fans.yaml
}
