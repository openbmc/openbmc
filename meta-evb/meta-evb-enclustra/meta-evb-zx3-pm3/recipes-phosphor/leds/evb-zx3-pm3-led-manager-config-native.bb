SUMMARY = "Phosphor LED Group Management for Enclustra Mars ZX3"
PR = "r1"

inherit native
inherit obmc-phosphor-license

PROVIDES += "virtual/phosphor-led-manager-config-native"

SRC_URI += "file://led.yaml"
S = "${WORKDIR}"

# Copies example led layout yaml file
do_install() {
    SRC=${S}
    DEST=${D}${datadir}/phosphor-led-manager
    install -D ${SRC}/led.yaml ${DEST}/led.yaml
}
