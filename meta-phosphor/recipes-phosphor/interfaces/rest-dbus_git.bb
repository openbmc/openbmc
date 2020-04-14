SUMMARY = "Phosphor OpenBMC REST framework"
DESCRIPTION = "Phosphor OpenBMC REST to DBUS daemon."
HOMEPAGE = "http://github.com/openbmc/rest-dbus"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit allarch
inherit obmc-phosphor-systemd
inherit setuptools3

RDEPENDS_${PN} += " \
        python-netserver \
        python-json \
        python-dbus \
        python-xml \
        pyphosphor-dbus \
        "

SYSTEMD_SERVICE_${PN} += "rest-dbus.service rest-dbus.socket"

SRC_URI += "git://github.com/openbmc/rest-dbus.git"

SRCREV = "9273a302e8f2b3c3e939dff77758e90f163bf6a1"

S = "${WORKDIR}/git"
