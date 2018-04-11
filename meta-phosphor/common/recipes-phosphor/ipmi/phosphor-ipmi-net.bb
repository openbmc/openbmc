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
SRCREV = "62ec622e0b71fb396ef681ad4cadc4a567ce5d85"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} = " \
        ${PN}.service \
        ${PN}.socket \
        "
