SUMMARY = "Phosphor LED Group Management with example data"
PROVIDES += "virtual/phosphor-led-manager-config-native"
PV = "1.0+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"

inherit native

# Copies example led layout yaml file
do_install() {
    SRC=${S}
    DEST=${D}${datadir}/phosphor-led-manager
    install -D ${SRC}/led.yaml ${DEST}/led.yaml
}

require phosphor-led-manager.inc
