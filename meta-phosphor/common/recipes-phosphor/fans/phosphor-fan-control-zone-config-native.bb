SUMMARY = "Phosphor fan zone definition default data"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-fan

PROVIDES += "virtual/phosphor-fan-control-zone-config"

FILES_${PN} += "phosphor-fan-control-zone-config"

SRC_URI += "file://zones.yaml"

S = "${WORKDIR}"

do_install() {
    DEST=${D}${control_datadir}
    install -D zones.yaml ${DEST}/zones.yaml
}
