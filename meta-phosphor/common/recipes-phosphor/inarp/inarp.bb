SUMMARY = "Inverse ARP daemon"
DESCRIPTION = "Daemon to respond to Inverse-ARP requests"
HOMEPAGE = "http://github.com/openbmc/inarp"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-c-daemon

TARGET_CFLAGS   += "-fpic -O2"

RDEPENDS_${PN} += "network"
SRC_URI += "git://github.com/openbmc/inarp"

SRCREV = "e3f27cf06cc2ca93ae9746ba705a0d9fc307cec2"

S = "${WORKDIR}/git"
INSTALL_NAME = "inarp"
