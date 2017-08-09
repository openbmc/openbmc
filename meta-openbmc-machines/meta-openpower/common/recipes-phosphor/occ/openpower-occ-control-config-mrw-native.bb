SUMMARY = "OpenPower OCC Control with MRW generated sensor IDs"
PR = "r1"

inherit native
inherit obmc-phosphor-utils
inherit obmc-phosphor-license
inherit openpower-occ-control

PROVIDES += "virtual/openpower-occ-control-config-native"
DEPENDS += "mrw-native mrw-perl-tools-native"

# Generate a YAML files based on MRW input
do_install_append() {
    install -d ${YAML_DEST}/
    ${STAGING_BINDIR_NATIVE}/perl-native/perl \
    ${STAGING_BINDIR_NATIVE}/gen_occ_map.pl \
    -i ${STAGING_DATADIR_NATIVE}/obmc-mrw/${MACHINE}.xml \
    -o ${YAML_DEST}/occ_sensor.yaml
}
