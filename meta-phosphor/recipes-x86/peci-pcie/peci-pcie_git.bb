SUMMARY = "PECI PCIe"
DESCRIPTION = "Gathers PCIe information using PECI \
and provides it on D-Bus"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
inherit cmake systemd

SRC_URI = "git://github.com/openbmc/peci-pcie"

DEPENDS = "boost sdbusplus libpeci"

PV = "0.1+git${SRCPV}"
SRCREV = "0b79f3e485554957a4b24d5f0cefc5bc577ad301"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.PCIe.service"

EXTRA_OECMAKE = "-DYOCTO=1"
