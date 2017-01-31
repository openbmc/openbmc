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
    ${PN}-bmc \
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
DEPENDS += "phosphor-dbus-interfaces"
RDEPENDS_${PN} += "sdbusplus"

RDEPENDS_${PN}-host += "libsystemd phosphor-dbus-interfaces"
RDEPENDS_${PN}-chassis += "libsystemd phosphor-dbus-interfaces"
RDEPENDS_${PN}-bmc += "libsystemd phosphor-dbus-interfaces"

PROVIDES += "virtual/obmc-host-state-mgmt"
RPROVIDES_${PN}-host += "virtual-obmc-host-state-mgmt"
FILES_${PN}-host = "${sbindir}/phosphor-host-state-manager"
DBUS_SERVICE_${PN}-host += "xyz.openbmc_project.State.Host.service"

PROVIDES += "virtual/obmc-chassis-state-mgmt"
RPROVIDES_${PN}-chassis += "virtual-obmc-chassis-state-mgmt"
FILES_${PN}-chassis = "${sbindir}/phosphor-chassis-state-manager"
DBUS_SERVICE_${PN}-chassis += "xyz.openbmc_project.State.Chassis.service"

PROVIDES += "virtual/obmc-bmc-state-mgmt"
RPROVIDES_${PN}-bmc += "virtual-obmc-bmc-state-mgmt"
FILES_${PN}-bmc = "${sbindir}/phosphor-bmc-state-manager"
DBUS_SERVICE_${PN}-bmc += "xyz.openbmc_project.State.BMC.service"

SRC_URI += "git://github.com/openbmc/phosphor-state-manager"
SRCREV = "20a4887fa02492d854ea131ffd5775512196e975"

S = "${WORKDIR}/git"
