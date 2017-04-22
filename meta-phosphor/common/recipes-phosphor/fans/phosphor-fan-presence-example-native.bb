SUMMARY = "Phosphor Fan Presence Detection example data"
PR = "r1"

require phosphor-fan.inc

inherit native
inherit obmc-phosphor-license
inherit phosphor-fan

PROVIDES += "virtual/phosphor-fan-presence-config"

S = "${WORKDIR}/git"

do_install() {
    DEST=${D}${presence_datadir}
    install -D ${S}/presence/example/fan-detect.yaml ${DEST}/config.yaml
}
