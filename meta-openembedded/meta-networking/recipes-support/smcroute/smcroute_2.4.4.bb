SUMMARY = "Static Multicast Routing Daemon"
DESCRIPTION = "SMCRoute is a daemon and command line tool to manipulate the multicast routing table in the UNIX kernel."
HOMEPAGE = "http://troglobit.github.io/smcroute.html"
SECTION = "net"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRCREV = "a8e5847e5f7e411be424f9b52a6cdf9d2ed4aeb5"
SRC_URI = "git://github.com/troglobit/smcroute.git;branch=master;protocol=git"

S = "${WORKDIR}/git"

inherit autotools
