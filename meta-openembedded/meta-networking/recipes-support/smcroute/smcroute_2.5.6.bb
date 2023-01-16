SUMMARY = "Static Multicast Routing Daemon"
DESCRIPTION = "SMCRoute is a daemon and command line tool to manipulate the multicast routing table in the UNIX kernel."
HOMEPAGE = "http://troglobit.github.io/smcroute.html"
SECTION = "net"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "999bdd724a1f963ac8bfd0598ffdd2a3d651646e"
SRC_URI = "git://github.com/troglobit/smcroute.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
