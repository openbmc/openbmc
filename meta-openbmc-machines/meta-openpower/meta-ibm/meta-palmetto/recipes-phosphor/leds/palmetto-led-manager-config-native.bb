SUMMARY = "Phosphor LED Group Management for Palmetto"
PR = "r1"

inherit native
inherit obmc-phosphor-utils
require obmc-phosphor-license

PROVIDES += "virtual/phosphor-led-manager-config"

S = "${WORKDIR}/git"

# Copies example led layout yaml file
do_install() {
    SRC=${S}
    DEST=${STAGING_DATADIR_NATIVE}/phosphor-led-manager
    install -D ${SRC}/led.yaml ${DEST}/led.yaml
}
