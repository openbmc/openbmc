SUMMARY = "Phosphor LED Group Management for Romulus"
PR = "r1"

inherit native
inherit obmc-phosphor-utils
inherit obmc-phosphor-license

PROVIDES += "virtual/phosphor-led-manager-config-native"

SRC_URI += "file://led.yaml"
S = "${WORKDIR}"

# Overwrite the example led layout yaml file prior
# to building the phosphor-led-manager package
do_install() {
    SRC=${S}
    DEST=${D}${datadir}/phosphor-led-manager
    install -D ${SRC}/led.yaml ${DEST}/led.yaml
}
