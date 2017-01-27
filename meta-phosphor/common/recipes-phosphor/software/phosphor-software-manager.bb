SUMMARY = "Phosphor Software Management"
DESCRIPTION = "Phosphor Software Manager provides a set of system software \
management daemons. It is suitable for use on a wide variety of OpenBMC \
platforms."
HOMEPAGE = "https://github.com/openbmc/phosphor-bmc-code-mgmt"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SOFTWARE_MGR_PACKAGES = " \
    ${PN}-version \
"
PACKAGES =+ "${SOFTWARE_MGR_PACKAGES}"
DBUS_PACKAGES = "${SOFTWARE_MGR_PACKAGES}"

# Set SYSTEMD_PACKAGES to empty because we do not want ${PN} and DBUS_PACKAGES
# handles the rest.
SYSTEMD_PACKAGES = ""

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"

RDEPENDS_${PN}-version += "libsystemd"

PROVIDES += "virtual/obmc-version-software-mgmt"
RPROVIDES_${PN}-version += "virtual-obmc-version-software-mgmt"
FILES_${PN}-version = "${sbindir}/phosphor-version-software-manager"
DBUS_SERVICE_${PN}-version += "xyz.openbmc_project.Software.Version.service"

SRC_URI += "git://github.com/openbmc/phosphor-bmc-code-mgmt"
SRCREV = "d613b8166a3c3dc652badf8d8c52e74492941f28"

S = "${WORKDIR}/git"
