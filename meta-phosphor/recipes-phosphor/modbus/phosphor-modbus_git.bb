SUMMARY = "phosphor-modbus"
DESCRIPTION = "Modbus inventory, sensors and firmware update service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS = " \
    ${PYTHON_PN}-jsonschema-native \
    phosphor-dbus-interfaces \
    phosphor-logging \
    sdbusplus \
    "

SRCREV = "878e44a9e32c3f06b0c36722ca9ded3bc99d711f"

PACKAGECONFIG ??= " \
    modbus-rtu \
    "

PACKAGECONFIG[modbus-rtu] = "-Dmodbus-rtu=enabled, -Dmodbus-rtu=disabled"
PV = "0.1+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/phosphor-modbus.git;branch=main;protocol=https"

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'modbus-rtu', \
                                               'xyz.openbmc_project.ModbusRTU.service', \
                                               '', d)}"

inherit pkgconfig meson systemd python3native

EXTRA_OEMESON:append = " -Dtests=disabled"
