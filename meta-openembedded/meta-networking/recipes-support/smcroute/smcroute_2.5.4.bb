SUMMARY = "Static Multicast Routing Daemon"
DESCRIPTION = "SMCRoute is a daemon and command line tool to manipulate the multicast routing table in the UNIX kernel."
HOMEPAGE = "http://troglobit.github.io/smcroute.html"
SECTION = "net"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "9b6e397595a154c84a329c55bed21e0c43e89ee6"
SRC_URI = "git://github.com/troglobit/smcroute.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
