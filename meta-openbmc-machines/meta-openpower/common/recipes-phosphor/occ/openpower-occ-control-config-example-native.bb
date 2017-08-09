SUMMARY = "OpenPower OCC Control with example occ sensor IDs"
PR = "r1"

inherit native
inherit obmc-phosphor-utils
require openpower-occ-control.inc

PROVIDES += "virtual/openpower-occ-control-config-native"

S = "${WORKDIR}/git"

# Copies example occ sensor ID yaml file
do_install() {
    SRC=${S}
    DEST=${D}${datadir}/openpower-occ-control
    install -D ${SRC}/example/occ_sensor.yaml ${DEST}/occ_sensor.yaml
}
