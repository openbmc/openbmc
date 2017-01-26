SUMMARY = "Phosphor OpenBMC REST framework"
DESCRIPTION = "Phosphor OpenBMC REST to DBUS daemon."
HOMEPAGE = "http://github.com/openbmc/rest-dbus"
PR = "r1"

inherit allarch
inherit obmc-phosphor-license
inherit obmc-phosphor-systemd
inherit setuptools

RDEPENDS_${PN} += " \
        python-netserver \
        python-json \
        python-dbus \
        python-xml \
        pyphosphor-dbus \
        "

SYSTEMD_SERVICE_${PN} += "rest-dbus.service rest-dbus.socket"

SRC_URI += "git://github.com/openbmc/rest-dbus.git"

SRCREV = "5a65d2f605494d5c308171ba4fd1ab08d7b3a91d"

S = "${WORKDIR}/git"
