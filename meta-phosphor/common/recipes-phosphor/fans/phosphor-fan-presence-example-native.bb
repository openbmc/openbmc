SUMMARY = "Phosphor Fan Presence Detection example data"
PR = "r1"

require phosphor-fan-presence.inc

inherit native
inherit obmc-phosphor-license
inherit phosphor-fan-presence

PROVIDES += "virtual/phosphor-fan-presence-config"

S = "${WORKDIR}/git"

do_install() {
    DEST=${D}${presence_datadir}
    install -D ${S}/example/fan-detect.yaml ${DEST}/config.yaml
}
