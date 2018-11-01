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
SRCREV = "6d579909fc8e623e8a0dd6d4a32a4aee725c32f7"

S = "${WORKDIR}/git"

exec_prefix="/usr/local"
