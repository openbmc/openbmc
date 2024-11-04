SUMMARY = "Phosphor Time Manager daemon"
DESCRIPTION = "Daemon to cater to BMC and HOST time management"
HOMEPAGE = "http://github.com/openbmc/phosphor-time-manager"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "systemd"
DEPENDS += "sdbusplus"
DEPENDS += "${PYTHON_PN}-sdbus++-native"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"
SRCREV = "f90892d4592a8d1bf9f32bed51e62c0a7eb9a200"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-time-manager;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig python3native
inherit obmc-phosphor-dbus-service

EXTRA_OEMESON = " -Dtests=disabled"

RDEPENDS:${PN} += "phosphor-settings-manager"
RDEPENDS:${PN} += "phosphor-network"
RDEPENDS:${PN} += "phosphor-objmgr"

DBUS_SERVICE:${PN} += "xyz.openbmc_project.Time.Manager.service"
