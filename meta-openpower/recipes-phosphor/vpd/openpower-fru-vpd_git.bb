SUMMARY = "Parser for OpenPOWER-format FRU VPD"
DESCRIPTION = "Parse OpenPOWER-format FRU VPD and update inventory"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit meson pkgconfig
inherit openpower-fru-vpd
inherit python3native
inherit obmc-phosphor-systemd

require ${BPN}.inc

SRC_URI += "file://70-op-vpd.rules"

DEPENDS += " \
        virtual/openpower-fru-vpd-layout \
        virtual/openpower-fru-inventory \
        virtual/openpower-fru-properties \
        sdbusplus \
        phosphor-logging \
        ${PYTHON_PN}-mako-native \
        ${PYTHON_PN}-pyyaml-native \
        autoconf-archive-native \
        "

SYSTEMD_SERVICE_${PN} += "op-vpd-parser.service"

S = "${WORKDIR}/git"

EXTRA_OEMESON = " \
             -Dtests=disabled \
             -DFRU_YAML=${STAGING_DIR_NATIVE}${vpdlayout_datadir}/layout.yaml \
             -DPROP_YAML=${STAGING_DIR_NATIVE}${properties_datadir}/out.yaml \
             "

do_install_append() {
        SRC=${STAGING_DATADIR_NATIVE}${inventory_datadir_name}
        DEST=${D}${inventory_envdir}
        install -d ${DEST}
        install ${SRC}/inventory ${DEST}

        install -d ${D}/${base_libdir}/udev/rules.d/
        install -m0644 ${WORKDIR}/70-op-vpd.rules ${D}/${base_libdir}/udev/rules.d/
}
