SUMMARY = "Logging DBUS Object"
DESCRIPTION = "Logging DBUS Object"
HOMEPAGE = "https://github.com/openbmc/phosphor-logging"
PR = "r1"

inherit autotools pkgconfig
inherit pythonnative
inherit obmc-phosphor-license
inherit obmc-phosphor-dbus-service
inherit phosphor-dbus-interfaces

DBUS_SERVICE_${PN} += "xyz.openbmc_project.Logging.service"

DEPENDS += "autoconf-archive-native"
DEPENDS += "systemd"
DEPENDS += "python-mako-native"
DEPENDS += "python-pyyaml-native"
DEPENDS += "sdbusplus sdbusplus-native"
DEPENDS += "phosphor-dbus-interfaces"
RDEPENDS_${PN} += "sdbusplus phosphor-dbus-interfaces"
PROVIDES += "virtual/obmc-logging-mgmt"
RPROVIDES_${PN} += "virtual-obmc-logging-mgmt"

PACKAGE_BEFORE_PN = "${PN}-test"
FILES_${PN}-test = "${sbindir}/*-test"

SRC_URI += "git://github.com/openbmc/phosphor-logging"
SRCREV = "9cfe9f38d9ed2ddedea6d58040bed6bfc0831f08"

S = "${WORKDIR}/git"

EXTRA_OECONF = "YAML_DIR=${STAGING_DIR_NATIVE}${yaml_dir}"
