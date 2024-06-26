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

SYSTEMD_SERVICE:${PN} += "op-vpd-parser.service"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[ibm-parser] = "-Dibm-parser=enabled, -Dibm-parser=disabled, libgpiod nlohmann-json cli11"
PACKAGECONFIG[vpd-manager] = "-Dvpd-manager=enabled, -Dvpd-manager=disabled"

EXTRA_OEMESON = " \
             -Dtests=disabled \
             -DFRU_YAML=${STAGING_DIR_NATIVE}${vpdlayout_datadir}/layout.yaml \
             -DPROP_YAML=${STAGING_DIR_NATIVE}${properties_datadir}/out.yaml \
             "

do_install:append() {
        SRC=${STAGING_DATADIR_NATIVE}${inventory_datadir_name}
        DEST=${D}${inventory_envdir}
        install -d ${DEST}
        install ${SRC}/inventory ${DEST}

        install -d ${D}/${nonarch_base_libdir}/udev/rules.d/
        install -m0644 ${UNPACKDIR}/70-op-vpd.rules ${D}/${nonarch_base_libdir}/udev/rules.d/
}
