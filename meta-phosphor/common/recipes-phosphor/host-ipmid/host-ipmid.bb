SUMMARY = "Phosphor OpenBMC IPMI daemon"
DESCRIPTION = "Phosphor OpenBMC IPMI router and plugin libraries"
HOMEPAGE = "http://github.com/openbmc/phosphor-host-ipmid"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

DEPENDS += "glib-2.0"
SRC_URI += "git://github.com/openbmc/phosphor-host-ipmid"

SRCREV = "02d37aab090090af9a779f4039931a6721bff615"

S = "${WORKDIR}/git"

do_install() {
        install -m 0755 -d ${D}${sbindir} ${D}${libdir} ${D}${libdir}/host-ipmid/
        install -m 0755 ${S}/ipmid ${D}${sbindir}/
        install -m 0755 ${S}/libapphandler.so ${D}${libdir}/host-ipmid/
}
