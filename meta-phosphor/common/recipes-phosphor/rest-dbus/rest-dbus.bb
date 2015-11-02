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
        obmc-mapper \
        "

SRC_URI += " \
        git://github.com/openbmc/rest-dbus.git \
        file://rest-dbus.service \
        "

SRCREV = "b10a57e2f34e94b13697328cc22f876f738feb0e"

S = "${WORKDIR}/git"
