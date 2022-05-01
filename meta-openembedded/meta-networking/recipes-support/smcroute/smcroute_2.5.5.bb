SUMMARY = "Static Multicast Routing Daemon"
DESCRIPTION = "SMCRoute is a daemon and command line tool to manipulate the multicast routing table in the UNIX kernel."
HOMEPAGE = "http://troglobit.github.io/smcroute.html"
SECTION = "net"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "9ca7441add4427a91fe90c34ae4a178ed9a50553"
SRC_URI = "git://github.com/troglobit/smcroute.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
