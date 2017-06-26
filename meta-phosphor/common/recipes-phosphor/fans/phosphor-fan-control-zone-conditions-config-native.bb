SUMMARY = "Phosphor zone conditions definition default data"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-fan

SRC_URI += "file://zone_conditions.yaml"

S = "${WORKDIR}"

do_install() {
    DEST=${D}${control_datadir}
    install -D zone_conditions.yaml ${DEST}/zone_conditions.yaml
}
