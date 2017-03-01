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
PACKAGE_BEFORE_PN = "${PN}-version"
PACKAGES_remove = "${PN}"
RDEPENDS_${PN}-dev = "${SOFTWARE_MGR_PACKAGES}"
RDEPENDS_${PN}-staticdev = "${SOFTWARE_MGR_PACKAGES}"

DBUS_PACKAGES = "${SOFTWARE_MGR_PACKAGES}"

# Set SYSTEMD_PACKAGES to empty because we do not want ${PN} and DBUS_PACKAGES
# handles the rest.
SYSTEMD_PACKAGES = ""

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"

RDEPENDS_${PN}-version += "phosphor-dbus-interfaces sdbusplus"

FILES_${PN}-version = "${sbindir}/phosphor-version-software-manager"
DBUS_SERVICE_${PN}-version += "xyz.openbmc_project.Software.BMC.Version.service"

SRC_URI += "git://github.com/openbmc/phosphor-bmc-code-mgmt"
SRCREV = "af4759e828690e51035c9d11c2a6845d9bb38406"

S = "${WORKDIR}/git"
