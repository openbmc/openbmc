SUMMARY = "Phosphor LED Group Management with MRW generated data"
PR = "r1"

inherit native
inherit obmc-phosphor-utils
inherit obmc-phosphor-license

DEPENDS += "mrw-native mrw-perl-tools-native"

# Generate a YAML files based on MRW input
do_install_append() {
    USE_MRW="${@cf_enabled(d, 'obmc-mrw', 'yes')}"
    DEST=${STAGING_DATADIR_NATIVE}/phosphor-led-manager

    if [ "${USE_MRW}" = "yes" ]; then
        install -d ${DEST}/
        ${STAGING_BINDIR_NATIVE}/perl-native/perl \
        ${STAGING_BINDIR_NATIVE}/gen_led_groups.pl \
        -i ${STAGING_DATADIR_NATIVE}/obmc-mrw/${MACHINE}.xml \
        -o ${DEST}/led.yaml
    fi
}
