SUMMARY = "PECI PCIe"
DESCRIPTION = "Gathers PCIe information using PECI \
and provides it on D-Bus"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
inherit cmake pkgconfig systemd

SRC_URI = "git://github.com/openbmc/peci-pcie;branch=master;protocol=https"

DEPENDS = "boost sdbusplus libpeci"

PV = "0.1+git${SRCPV}"
SRCREV = "328685e5962fb4e563f603fb662633fed00aff0e"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE:${PN} += "xyz.openbmc_project.PCIe.service"

EXTRA_OECMAKE = "-DYOCTO=1"
