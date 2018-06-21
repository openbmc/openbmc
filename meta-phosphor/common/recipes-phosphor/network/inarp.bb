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
SRCREV = "e0127d27473ca37ded48813e2ed571abada81357"

S = "${WORKDIR}/git"

exec_prefix="/usr/local"
