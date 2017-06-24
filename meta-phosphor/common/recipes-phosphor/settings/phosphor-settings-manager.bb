SUMMARY = "Phosphor Settings Manager"
DESCRIPTION = "Phosphor Settings Manager is an application that creates \
d-bus objects to represent various user settings."
PR = "r1"

inherit autotools
inherit obmc-phosphor-dbus-service
inherit pythonnative
inherit phosphor-settings-manager

require phosphor-settings-manager.inc

DBUS_SERVICE_${PN} = "xyz.openbmc_project.Settings.service"

DEPENDS += "python-pyyaml-native"
DEPENDS += "python-mako-native"
DEPENDS += "autoconf-archive-native"
DEPENDS += "virtual/phosphor-settings-defaults"
DEPENDS += "sdbusplus sdbusplus-native"
DEPENDS += "phosphor-dbus-interfaces phosphor-dbus-interfaces-native"
DEPENDS += "cereal"

RDEPENDS_${PN} += "sdbusplus phosphor-dbus-interfaces"

S = "${WORKDIR}/git"

EXTRA_OECONF = " \
             SETTINGS_YAML=${STAGING_DIR_NATIVE}${settings_datadir}/defaults.yaml \
             "
