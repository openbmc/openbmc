SUMMARY = "Settings DBUS object"
DESCRIPTION = "Settings DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-settingsd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd
inherit setuptools

DEPENDS += "python-pyyaml-native"
RDEPENDS_${PN} += "python-dbus python-pygobject"
PROVIDES += "virtual/obmc-settings-mgmt"
RPROVIDES_${PN} += "virtual-obmc-settings-mgmt"

SRC_URI += "git://github.com/openbmc/phosphor-settingsd"

SRCREV = "54be910bd0af885217adc7dfcd95b7db9a13886d"

S = "${WORKDIR}/git"

