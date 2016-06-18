SUMMARY = "Inverse ARP daemon"
DESCRIPTION = "Daemon to respond to Inverse-ARP requests"
HOMEPAGE = "http://github.com/openbmc/inarp"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-c-daemon

TARGET_CFLAGS   += "-fpic -O2"

RDEPENDS_${PN} += "network"
SRC_URI += "git://github.com/openbmc/inarp"

SRCREV = "04d1f97f2e6e471d63c7d56dce7bd8472eb8fbfb"

S = "${WORKDIR}/git"
INSTALL_NAME = "inarp"
