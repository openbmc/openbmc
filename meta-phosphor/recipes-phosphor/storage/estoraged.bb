SUMMARY = "Encrypted storage management daemon"
DESCRIPTION = "Provides a D-Bus interface to manage an encrypted storage device"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit meson pkgconfig systemd

DEPENDS += "systemd"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "cryptsetup"
DEPENDS += "openssl"
DEPENDS += "phosphor-logging"
DEPENDS += "sdbusplus"
DEPENDS += "stdplus"

RDEPENDS:${PN} += "e2fsprogs"

EXTRA_OEMESON = "-Dtests=disabled"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/estoraged.git;branch=master;protocol=https"
SRCREV = "da5e96a7e322d4a6bfef21438266114e55682161"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += "xyz.openbmc_project.eStoraged.service"
