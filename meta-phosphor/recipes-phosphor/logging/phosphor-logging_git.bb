SUMMARY = "Phosphor OpenBMC event and error logging"
DESCRIPTION = "An error and event log daemon application, and \
               supporting tools for OpenBMC."
HOMEPAGE = "https://github.com/openbmc/phosphor-logging"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "systemd"
DEPENDS += "${PYTHON_PN}-mako-native"
DEPENDS += "${PYTHON_PN}-pyyaml-native"
DEPENDS += "${PYTHON_PN}-native"
DEPENDS += "${PYTHON_PN}-sdbus++-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "virtual/phosphor-logging-callouts"
DEPENDS += "libcereal"
DEPENDS += "sdeventplus"
DEPENDS += "packagegroup-obmc-yaml-providers"
DEPENDS += "dbus"
SRCREV = "d8ae618a44c554c8e817833e466f773ee2f4ff88"
PACKAGECONFIG ??= ""
PACKAGECONFIG[openpower-pels] = " \
        -Dopenpower-pel-extension=enabled, \
        -Dopenpower-pel-extension=disabled, \
        nlohmann-json cli11 libpldm python3, \
        python3, \
        "
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-logging;branch=master;protocol=https"

SYSTEMD_PACKAGES = "${LOGGING_PACKAGES}"
S = "${WORKDIR}/git"

inherit pkgconfig meson
inherit python3native
inherit obmc-phosphor-dbus-service
inherit phosphor-logging
inherit phosphor-dbus-yaml

def get_info_cap(d):
    flash_size = int(d.getVar('FLASH_SIZE') or 0)
    if flash_size <= 32768:
        return "10"
    elif flash_size <= 65536:
        return "128"
    else:
        return "256"

ERR_INFO_CAP ??= "${@get_info_cap(d)}"
ERR_INFO_CAP:df-phosphor-mmc ?= "256"

EXTRA_OEMESON = " \
        -Dtests=disabled \
        -Dyamldir=${STAGING_DIR_TARGET}${yaml_dir} \
        -Dcallout_yaml=${STAGING_DIR_NATIVE}${callouts_datadir}/callouts.yaml \
        -Derror_info_cap=${ERR_INFO_CAP} \
        "

FILES:${PN}-test = "${bindir}/*-test"
FILES:${PN}-base += " \
        ${datadir}/dbus-1 \
        ${bindir}/phosphor-log-manager \
        ${libdir}/libphosphor_logging.so.* \
        ${datadir}/dbus-1/system-services/xyz.openbmc_project.Logging.service \
"
FILES:phosphor-rsyslog-config += " \
        ${bindir}/phosphor-rsyslog-conf \
"

ALLOW_EMPTY:${PN} = "1"

USERADD_PACKAGES = "${PN}-base"

PACKAGE_BEFORE_PN = "${PN}-test"
# Package configuration
LOGGING_PACKAGES = " \
        ${PN}-base \
        phosphor-rsyslog-config \
"
PACKAGE_BEFORE_PN += "${LOGGING_PACKAGES}"
DBUS_PACKAGES = "${LOGGING_PACKAGES}"
GROUPADD_PARAM:${PN}-base = "-r phosphor-logging"
DBUS_SERVICE:${PN}-base += "xyz.openbmc_project.Logging.service"
DBUS_SERVICE:phosphor-rsyslog-config += "xyz.openbmc_project.Syslog.Config.service"
