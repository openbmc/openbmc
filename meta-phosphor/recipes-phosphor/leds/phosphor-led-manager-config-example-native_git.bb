SUMMARY = "Phosphor LED Group Management with example data"
PROVIDES += "virtual/phosphor-led-manager-config-native"
PV = "1.0+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"

inherit native

# Copies example led layout json file
do_install() {
    SRC=${S}
    install -m 0644 ${SRC}/led-group-config.json ${D}${datadir}/phosphor-led-manager/led.json
}

require phosphor-led-manager.inc
