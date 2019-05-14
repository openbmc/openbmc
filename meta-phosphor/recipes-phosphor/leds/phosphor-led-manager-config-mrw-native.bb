SUMMARY = "Phosphor LED Group Management with MRW generated data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit mrw-xml

PROVIDES += "virtual/phosphor-led-manager-config-native"
DEPENDS += "mrw-native mrw-perl-tools-native"

# Generate a YAML files based on MRW input
do_install_append() {
    USE_MRW="${@bb.utils.contains('DISTRO_FEATURES', 'obmc-mrw', 'yes', 'no', d)}"
    DEST=${D}${datadir}/phosphor-led-manager

    if [ "${USE_MRW}" = "yes" ]; then
        install -d ${DEST}/
        ${STAGING_BINDIR_NATIVE}/perl-native/perl \
        ${STAGING_BINDIR_NATIVE}/gen_led_groups.pl \
        -i ${mrw_datadir}/${MRW_XML} \
        -o ${DEST}/led.yaml
    fi
}
