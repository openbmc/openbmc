SUMMARY = "Phosphor LED Group Management with example data"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit native
require phosphor-led-manager.inc

PROVIDES += "virtual/phosphor-led-manager-config-native"

S = "${WORKDIR}/git"

# Copies example led layout yaml file
do_install() {
    SRC=${S}
    DEST=${D}${datadir}/phosphor-led-manager
    install -D ${SRC}/led.yaml ${DEST}/led.yaml
}
