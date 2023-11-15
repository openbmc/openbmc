LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
inherit meson pkgconfig systemd

PACKAGECONFIG ??= ""
PACKAGECONFIG[libpeci] = "-Dlibpeci=enabled,-Dlibpeci=disabled"
PACKAGECONFIG[crashdump] = "-Dcrashdump=enabled,-Dcrashdump=disabled"

SRC_URI = "git://github.com/openbmc/host-error-monitor;branch=master;protocol=https"

DEPENDS = "boost sdbusplus libgpiod libpeci"

PV = "0.1+git${SRCPV}"
SRCREV = "bf81ccf8cb8f0ab4c54843b1e41e8c34731c6df8"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE:${PN} += "xyz.openbmc_project.HostErrorMonitor.service"
