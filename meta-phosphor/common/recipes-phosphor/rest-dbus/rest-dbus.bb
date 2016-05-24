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
        pyphosphor \
        "

SRC_URI += " \
        git://github.com/openbmc/rest-dbus.git \
        file://rest-dbus.service \
        "

SRCREV = "64077101d9f2b6e51c897db82ffc0a399a34d15b"

S = "${WORKDIR}/git"
