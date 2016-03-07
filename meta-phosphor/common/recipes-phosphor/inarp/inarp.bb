SUMMARY = "Inverse ARP daemon"
DESCRIPTION = "Daemon to respond to Inverse-ARP requests"
HOMEPAGE = "http://github.com/openbmc/inarp"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-c-daemon

TARGET_CFLAGS   += "-fpic -O2"

RDEPENDS_${PN} += "network"
SRC_URI += "git://github.com/openbmc/inarp"

SRCREV = "19ed5170356495f5fc67189513c5739780ee6a81"

S = "${WORKDIR}/git"
INSTALL_NAME = "inarp"
