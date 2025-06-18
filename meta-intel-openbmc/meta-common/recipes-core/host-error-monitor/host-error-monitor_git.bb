LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
inherit meson pkgconfig systemd

PACKAGECONFIG ??= ""
PACKAGECONFIG[libpeci] = "-Dlibpeci=enabled,-Dlibpeci=disabled"
PACKAGECONFIG[crashdump] = "-Dcrashdump=enabled,-Dcrashdump=disabled"
PACKAGECONFIG[send-to-logger] = "-Dsend-to-logger=enabled,-Dsend-to-logger=disabled"

EXTRA_OEMESON += "-Dtests=disabled"

SRC_URI = "git://github.com/openbmc/host-error-monitor;branch=master;protocol=https"

DEPENDS = "boost sdbusplus libgpiod libpeci phosphor-dbus-interfaces"

PV = "0.1+git${SRCPV}"
SRCREV = "4dd67b099f4b45814a71da3a155fbad34d2f13fc"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE:${PN} += "xyz.openbmc_project.HostErrorMonitor.service"
