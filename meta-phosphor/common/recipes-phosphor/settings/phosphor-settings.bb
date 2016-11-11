SUMMARY = "Settings DBUS object"
DESCRIPTION = "Settings DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-settingsd"
PR = "r1"

inherit allarch
inherit obmc-phosphor-license
inherit setuptools
inherit obmc-phosphor-dbus-service

DBUS_SERVICE_${PN} = "org.openbmc.settings.Host.service"

DEPENDS += "python-pyyaml-native"
RDEPENDS_${PN} += "python-dbus python-pygobject"
PROVIDES += "virtual/obmc-settings-mgmt"
RPROVIDES_${PN} += "virtual-obmc-settings-mgmt"

SRC_URI += "git://github.com/openbmc/phosphor-settingsd"

SRCREV = "0fdfa72ee81bf52ba65bfc401d85d0e491abbf40"

S = "${WORKDIR}/git"
