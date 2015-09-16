SUMMARY = "Phosphor OpenBMC REST framework"
DESCRIPTION = "Phosphor OpenBMC REST to DBUS daemon."
HOMEPAGE = "http://github.com/openbmc/rest-dbus"
PR = "r1"

inherit allarch
inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

SYSTEMD_SERVICE_${PN} = "${PN}.service"

RDEPENDS_${PN} += " \
        python-netserver \
        python-json \
        python-dbus \
        python-xml \
        "
SRC_URI += " \
        git://github.com/openbmc/rest-dbus.git \
        file://makefile.patch \
        file://resources-path.patch \
        file://no-session-bus.patch \
        "

SRCREV = "9b0e0bab5150ccc4333ce442932f8969f365155e"

S = "${WORKDIR}/git"

do_install() {
        oe_runmake prefix=${D} install
}
