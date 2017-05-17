SUMMARY = "Settings DBUS object"
DESCRIPTION = "Settings DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-settingsd"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools
inherit obmc-phosphor-dbus-service
inherit pythonnative
inherit phosphor-settings-manager

DBUS_SERVICE_${PN} = "xyz.openbmc_project.Settings.service"

DEPENDS += "python-pyyaml-native"
DEPENDS += "python-mako-native"
DEPENDS += "autoconf-archive-native"
DEPENDS += "virtual/phosphor-settings-defaults"
DEPENDS += "sdbusplus sdbusplus-native"
DEPENDS += "phosphor-dbus-interfaces phosphor-dbus-interfaces-native"

RDEPENDS_${PN} += "sdbusplus phosphor-dbus-interfaces"

SRC_URI += "git:///esw/san5/dkodihal/obmc/phosphor-settingsd"
SRCREV = "4198292c196d83329cf2f7e71a2324cc5966e7c0"

S = "${WORKDIR}/git"

EXTRA_OECONF = " \
             SETTINGS_YAML=${STAGING_DIR_NATIVE}${settings_datadir}/defaults.yaml \
             "
