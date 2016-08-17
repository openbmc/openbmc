SUMMARY = "Static Multicast Routing Daemon"
DESCRIPTION = "SMCRoute is a daemon and command line tool to manipulate the multicast routing table in the UNIX kernel."
HOMEPAGE = "http://troglobit.github.io/smcroute.html"
SECTION = "net"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4325afd396febcb659c36b49533135d4"

SRCREV = "d6280e64b27d5a4bd7f37dac36b455f4ae5f9ab3"
SRC_URI = "git://github.com/troglobit/smcroute.git;branch=master;protocol=git"

S = "${WORKDIR}/git"

inherit autotools
