SUMMARY = "PECI Library"
DESCRIPTION = "PECI Library"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
SRCREV = "380998bc0e0c58bd6b3d35aaac06b84d5a49b4b5"
PACKAGECONFIG ??= ""
PACKAGECONFIG[dbus-raw-peci] = "-DDBUS_RAW_PECI=ON,-DDBUS_RAW_PECI=OFF,boost sdbusplus"
PV = "0.1+git${SRCPV}"

SRC_URI = "git://github.com/openbmc/libpeci;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'dbus-raw-peci', 'com.intel.peci.service', '', d)}"

inherit cmake pkgconfig systemd
