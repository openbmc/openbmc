LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
inherit meson pkgconfig systemd

PACKAGECONFIG ??= ""
PACKAGECONFIG[libpeci] = "-Dlibpeci=enabled,-Dlibpeci=disabled"
PACKAGECONFIG[crashdump] = "-Dcrashdump=enabled,-Dcrashdump=disabled"

SRC_URI = "git://github.com/openbmc/host-error-monitor;branch=master;protocol=https"

DEPENDS = "boost sdbusplus libgpiod libpeci"

PV = "0.1+git${SRCPV}"
SRCREV = "8c01b421eaeef317464718143b449d82b350563b"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE:${PN} += "xyz.openbmc_project.HostErrorMonitor.service"
