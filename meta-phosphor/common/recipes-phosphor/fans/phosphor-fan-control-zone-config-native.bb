SUMMARY = "Phosphor fan zone definition default data"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-fan

SRC_URI += "file://zones.yaml"

S = "${WORKDIR}"

do_install() {
    DEST=${D}${control_datadir}
    install -D zones.yaml ${DEST}/zones.yaml
}
