SUMMARY = "OpenPower OCC Control with MRW generated sensor IDs"
PR = "r1"

inherit native
inherit obmc-phosphor-utils
inherit obmc-phosphor-license

PROVIDES += "virtual/openpower-occ-control-config-native"
DEPENDS += "mrw-native mrw-perl-tools-native"

# Generate a YAML files based on MRW input
do_install_append() {
    USE_MRW="${@cf_enabled(d, 'obmc-mrw', 'yes')}"
    DEST=${D}${datadir}/openpower-occ-control

    if [ "${USE_MRW}" = "yes" ]; then
        install -d ${DEST}/
        ${STAGING_BINDIR_NATIVE}/perl-native/perl \
        ${STAGING_BINDIR_NATIVE}/gen_occ_map.pl \
        -i ${STAGING_DATADIR_NATIVE}/obmc-mrw/${MACHINE}.xml \
        -o ${DEST}/occ_sensor.yaml
    fi
}
