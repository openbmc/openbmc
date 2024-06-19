SUMMARY = "Static Multicast Routing Daemon"
DESCRIPTION = "SMCRoute is a daemon and command line tool to manipulate the multicast routing table in the UNIX kernel."
HOMEPAGE = "http://troglobit.github.io/smcroute.html"
SECTION = "net"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "b315d06ddb0dad16a52fe91e5831b4030ce37010"
SRC_URI = "git://github.com/troglobit/smcroute.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
