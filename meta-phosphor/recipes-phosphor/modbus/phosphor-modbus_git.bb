SUMMARY = "phosphor-modbus"
DESCRIPTION = "Modbus inventory, sensors and firmware update service"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS = " \
    phosphor-dbus-interfaces \
    phosphor-logging \
    sdbusplus \
    "

SRCREV = "978c7809923b3ef62a39ab404f35093ea39fcdc7"

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
