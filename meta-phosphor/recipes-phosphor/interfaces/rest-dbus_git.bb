SUMMARY = "Phosphor OpenBMC REST framework"
DESCRIPTION = "Phosphor OpenBMC REST to DBUS daemon."
HOMEPAGE = "http://github.com/openbmc/rest-dbus"
PR = "r1"
PV = "1.0+git${SRCPV}"

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

SRCREV = "403ce23066a8b0f0dca23220530b8760748b168e"

S = "${WORKDIR}/git"
