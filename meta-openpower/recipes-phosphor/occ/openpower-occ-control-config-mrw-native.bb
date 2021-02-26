SUMMARY = "OpenPower OCC Control with MRW generated sensor IDs"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit openpower-occ-control
inherit mrw-xml
inherit native

PROVIDES += "virtual/openpower-occ-control-config-native"
DEPENDS += "mrw-native mrw-perl-tools-native"

# Generate a YAML files based on MRW input
do_install_append() {
    install -d ${YAML_DEST}/
    ${STAGING_BINDIR_NATIVE}/perl-native/perl \
    ${STAGING_BINDIR_NATIVE}/gen_occ_map.pl \
    -i ${mrw_datadir}/${MRW_XML} \
    -o ${YAML_DEST}/occ_sensor.yaml
}
