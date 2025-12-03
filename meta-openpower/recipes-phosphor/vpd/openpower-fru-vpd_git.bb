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
        libgpiod \
        phosphor-dbus-interfaces \
        "

SYSTEMD_SERVICE:${PN} += "op-vpd-parser.service"
SYSTEMD_SERVICE:${PN} += "vpd-manager.service"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[ibm_system] = "-Dibm_system=enabled, -Dibm_system=disabled, nlohmann-json cli11"

EXTRA_OEMESON = " \
             -Dtests=disabled \
             "

do_install:append() {
        SRC=${STAGING_DATADIR_NATIVE}${inventory_datadir_name}
        DEST=${D}${inventory_envdir}
        install -d ${DEST}
        install ${SRC}/inventory ${DEST}

        install -d ${D}/${nonarch_base_libdir}/udev/rules.d/
        install -m0644 ${UNPACKDIR}/70-op-vpd.rules ${D}/${nonarch_base_libdir}/udev/rules.d/
}
