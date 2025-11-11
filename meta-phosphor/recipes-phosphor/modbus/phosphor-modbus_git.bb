SUMMARY = "phosphor-modbus"
DESCRIPTION = "Modbus inventory, sensors and firmware update service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS = " \
    phosphor-dbus-interfaces \
    phosphor-logging \
    sdbusplus \
    "

SRCREV = "e92aba4516471f5a01d4ab1f93eb9919ec05c21f"

PACKAGECONFIG ??= " \
    modbus-rtu \
    "

PACKAGECONFIG[modbus-rtu] = "-Dmodbus-rtu=enabled, -Dmodbus-rtu=disabled"
PV = "0.1+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/phosphor-modbus.git;branch=main;protocol=https"

SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'modbus-rtu', \
                                               'xyz.openbmc_project.ModbusRTU.service', \
                                               '', d)}"
S = "${WORKDIR}/git"

inherit pkgconfig meson systemd

EXTRA_OEMESON:append = " -Dtests=disabled"
