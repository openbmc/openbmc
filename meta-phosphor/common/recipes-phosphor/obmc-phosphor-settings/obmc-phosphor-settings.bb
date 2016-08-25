SUMMARY = "Settings DBUS object"
DESCRIPTION = "Settings DBUS object"
HOMEPAGE = "http://github.com/openbmc/phosphor-settingsd"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd
inherit obmc-phosphor-settings-mgmt
inherit setuptools

RDEPENDS_${PN} += "python-dbus python-pygobject"

SRC_URI += "git:////esw/san2/anoo/github/anoo1/phosphor-settingsd;branch=0824_pysetup"

SRCREV = "22ca1b71c20007c04c8ddbb2a6c3a2497ffaeb40"

S = "${WORKDIR}/git"

