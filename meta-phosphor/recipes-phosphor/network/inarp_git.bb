SUMMARY = "Inverse ARP daemon"
DESCRIPTION = "Daemon to respond to Inverse-ARP requests"
HOMEPAGE = "http://github.com/openbmc/inarp"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit autotools
inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

DEPENDS += "autoconf-archive-native"
RDEPENDS_${PN} += "network"

SRC_URI += "git://github.com/openbmc/inarp"
SRCREV = "6e296617f041d7665b2540151646af452088482c"

S = "${WORKDIR}/git"

exec_prefix="/usr/local"
