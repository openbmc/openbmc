SUMMARY = "Phosphor State Management"
DESCRIPTION = "Phosphor State Manager provides a set of system state \
management daemons. It is suitable for use on a wide variety of OpenBMC \
platforms."
HOMEPAGE = "https://github.com/openbmc/phosphor-state-manager"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

STATE_MGR_PACKAGES = " \
    ${PN}-host \
    ${PN}-chassis \
"
PACKAGES =+ "${STATE_MGR_PACKAGES}"
DBUS_PACKAGES = "${STATE_MGR_PACKAGES}"

# Set SYSTEMD_PACKAGES to empty because we do not want ${PN} and DBUS_PACKAGES
# handles the rest.
SYSTEMD_PACKAGES = ""

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"

RDEPENDS_${PN}-host += "libsystemd"
RDEPENDS_${PN}-chassis += "libsystemd"

PROVIDES += "virtual/obmc-host-state-mgmt"
RPROVIDES_${PN}-host += "virtual-obmc-host-state-mgmt"
FILES_${PN}-host = "${sbindir}/phosphor-host-state-manager"
DBUS_SERVICE_${PN}-host += "xyz.openbmc_project.State.Host.service"

PROVIDES += "virtual/obmc-chassis-state-mgmt"
RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-state-mgmt"
FILES_${PN}-chassis = "${sbindir}/phosphor-chassis-state-manager"
DBUS_SERVICE_${PN}-chassis += "xyz.openbmc_project.State.Chassis.service"

SRC_URI += "git://github.com/openbmc/phosphor-state-manager"
SRCREV = "6e0b50936735faac96db2f20fe84ffd0733750a9"

S = "${WORKDIR}/git"
