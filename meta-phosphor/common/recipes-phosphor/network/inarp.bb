SUMMARY = "Inverse ARP daemon"
DESCRIPTION = "Daemon to respond to Inverse-ARP requests"
HOMEPAGE = "http://github.com/openbmc/inarp"
PR = "r1"

inherit autotools
inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

DEPENDS += "autoconf-archive-native"
RDEPENDS_${PN} += "network"

SRC_URI += "git://github.com/openbmc/inarp"
SRCREV = "2af0b2d9787e4fbb41f3714c182e8ea5f433e8bb"

S = "${WORKDIR}/git"

exec_prefix="/usr/local"
