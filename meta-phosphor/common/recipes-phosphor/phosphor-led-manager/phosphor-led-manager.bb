SUMMARY = "Phosphor LED Group Management Daemon"
DESCRIPTION = "Daemon to cater to triggering actions on LED groups"
HOMEPAGE = "http://github.com/openbmc/phosphor-led-manager"
PR = "r1"

inherit autotools pkgconfig
require ${PN}.inc

DEPENDS += "python-pyyaml-native"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus sdbusplus-native"
DEPENDS += "${PN}-config"
RDEPENDS_${PN} += "libsystemd"

S = "${WORKDIR}/git"

DBUS_SERVICE_${PN} += "xyz.openbmc_project.ledmanager.service"
