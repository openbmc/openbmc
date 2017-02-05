SUMMARY = "Parser for OpenPOWER-format FRU VPD"
DESCRIPTION = "Parse OpenPOWER-format FRU VPD and update ivnentory"
PR = "r1"

inherit autotools pkgconfig
inherit openpower-fru-vpd
inherit pythonnative

require ${PN}.inc

DEPENDS += " \
        virtual/openpower-fru-vpd-layout \
        virtual/openpower-fru-inventory \
        python-mako-native \
        python-pyyaml-native \
        phosphor-mapper \
        sdbusplus \
        phosphor-logging \
        autoconf-archive-native \
        "

S = "${WORKDIR}/git"

EXTRA_OECONF = "FRU_YAML=${STAGING_DIR_NATIVE}${vpdlayout_datadir}/layout.yaml"

do_install_append() {
        SRC=${STAGING_DATADIR_NATIVE}${inventory_datadir_name}
        DEST=${D}${inventory_datadir_target}
        install -d ${DEST}
        install ${SRC}/inventory ${DEST}
}
