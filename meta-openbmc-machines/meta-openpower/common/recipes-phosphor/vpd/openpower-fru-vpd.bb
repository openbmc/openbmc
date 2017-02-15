SUMMARY = "Parser for OpenPOWER-format FRU VPD"
DESCRIPTION = "Parse OpenPOWER-format FRU VPD and update ivnentory"
PR = "r1"

inherit autotools pkgconfig
inherit openpower-fru-vpd
inherit pythonnative
inherit obmc-phosphor-systemd

require ${PN}.inc

SRC_URI += "file://70-i2c.rules"

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

RDEPENDS_${PN} += "libsystemd"

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.OpenPOWER.Vpd.Parser@.service"

S = "${WORKDIR}/git"

EXTRA_OECONF = "FRU_YAML=${STAGING_DIR_NATIVE}${vpdlayout_datadir}/layout.yaml"

do_install_append() {
        SRC=${STAGING_DATADIR_NATIVE}${inventory_datadir_name}
        DEST=${D}${inventory_datadir_target}
        install -d ${DEST}
        install ${SRC}/inventory ${DEST}

        install -d ${D}/${base_libdir}/udev/rules.d/
        install ${WORKDIR}/70-i2c.rules ${D}/${base_libdir}/udev/rules.d/
}
