SUMMARY = "Phosphor zone events definition default data"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-fan

SRC_URI += "file://events.yaml"

S = "${WORKDIR}"

do_install() {
    DEST=${D}${control_datadir}
    install -D events.yaml ${DEST}/events.yaml
}
