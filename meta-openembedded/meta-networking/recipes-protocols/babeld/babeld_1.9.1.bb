SUMMARY = "Babel is a loop-avoiding distance-vector routing protocol"
DESCRIPTION = "\
Babel is a loop-avoiding distance-vector routing protocol for IPv6 and \
IPv4 with fast convergence properties. It is based on the ideas in DSDV, AODV \
and Cisco's EIGRP, but is designed to work well not only in wired networks \
but also in wireless mesh networks, and has been extended with support \
for overlay networks. Babel is in the process of becoming an IETF Standard. \
"
HOMEPAGE = "https://www.irif.fr/~jch/software/babel/"
SECTION = "net"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE;md5=411a48ac3c2e9e0911b8dd9aed26f754"

SRC_URI = "git://github.com/jech/babeld.git;protocol=https;branch=master"
SRCREV = "0835d5d894ea016ab7b81562466cade2c51a12d4"

UPSTREAM_CHECK_GITTAGREGEX = "babeld-(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

do_compile () {
	oe_runmake babeld
}

do_install () {
	oe_runmake install.minimal PREFIX=${D}
}

