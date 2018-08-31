SUMMARY = "Phosphor Network IPMI Daemon"
DESCRIPTION = "Daemon to support IPMI protocol over network"
HOMEPAGE = "https://github.com/openbmc/phosphor-net-ipmid"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

DEPENDS += "autoconf-archive-native"
DEPENDS += "phosphor-mapper"
DEPENDS += "systemd"
DEPENDS += "phosphor-ipmi-host"
RDEPENDS_${PN} += "libmapper"
RDEPENDS_${PN} += "libsystemd"

SRC_URI += "git://github.com/openbmc/phosphor-net-ipmid"
SRCREV = "fe5a64587256eced4736cd70f23b5be58c8933a3"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} = " \
        ${PN}.service \
        ${PN}.socket \
        "
