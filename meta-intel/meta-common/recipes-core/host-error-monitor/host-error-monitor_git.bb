LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
inherit cmake systemd

SRC_URI = "git://github.com/openbmc/host-error-monitor"

DEPENDS = "boost sdbusplus libgpiod libpeci"

PV = "0.1+git${SRCPV}"
SRCREV = "5287c02c5f96b40f0941c9c72ab29d2c7ac44a96"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.HostErrorMonitor.service"

EXTRA_OECMAKE = "-DYOCTO=1"
