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

SRCREV = "3084b80c489c10b03207c8fb97d4437fd760f30d"

S = "${WORKDIR}/git"
