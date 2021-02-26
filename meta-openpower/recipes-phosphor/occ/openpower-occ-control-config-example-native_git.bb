SUMMARY = "OpenPower OCC Control with example occ sensor IDs"
PR = "r1"
PV = "1.0+git${SRCPV}"

require openpower-occ-control.inc
inherit openpower-occ-control
inherit native

PROVIDES += "virtual/openpower-occ-control-config-native"

S = "${WORKDIR}/git"

# Copies example occ sensor ID yaml file
do_install() {
    SRC=${S}
    install -d ${YAML_DEST}/
    install -D ${SRC}/example/occ_sensor.yaml ${YAML_DEST}/occ_sensor.yaml
}
