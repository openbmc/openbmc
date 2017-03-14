SUMMARY = "Phosphor LED Group Management Daemon"
DESCRIPTION = "Daemon to cater to triggering actions on LED groups"
PR = "r1"

inherit autotools pkgconfig pythonnative
inherit obmc-phosphor-dbus-service
require ${PN}.inc

DEPENDS += "python-pyyaml-native"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus sdbusplus-native"
DEPENDS += "phosphor-logging"

DEPENDS += "virtual/${PN}-config-native"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "phosphor-logging"

S = "${WORKDIR}/git"

DBUS_SERVICE_${PN} += "xyz.openbmc_project.LED.GroupManager.service"
SYSTEMD_SERVICE_${PN} += "obmc-led-group-start@.service obmc-led-group-stop@.service"

EXTRA_OECONF = "YAML_PATH=${STAGING_DATADIR_NATIVE}/${PN}"
