SUMMARY = "PECI Library"
DESCRIPTION = "PECI Library"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
inherit cmake pkgconfig systemd

SRC_URI = "git://github.com/openbmc/libpeci;branch=master;protocol=https"

PV = "0.1+git${SRCPV}"
SRCREV = "58dac1fdb2c4656f151509be4105ea0be3ab1988"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[dbus-raw-peci] = "-DDBUS_RAW_PECI=ON,-DDBUS_RAW_PECI=OFF,boost sdbusplus"
SYSTEMD_SERVICE:${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'dbus-raw-peci', 'com.intel.peci.service', '', d)}"
