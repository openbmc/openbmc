SUMMARY = "sends ICMP ECHO_REQUEST packets to network hosts"
DESCRIPTION = "fping is a ping like program which uses the Internet Control \
Message Protocol (ICMP) echo request to determine if a target host is \
responding. fping differs from ping in that you can specify any number of \
targets on the command line, or specify a file containing the lists of \
targets to ping.  Instead of sending to one target until it times out or \
replies, fping will send out a ping packet and move on to the next target \
in a round-robin fashion."
HOMEPAGE = "http://www.fping.org/"
SECTION = "net"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=c6170fbadddfcd74f011515291d96901"

SRC_URI = "http://www.fping.org/dist/fping-${PV}.tar.gz"
SRC_URI[sha256sum] = "ed38c0b9b64686a05d1b3bc1d66066114a492e04e44eef1821d43b1263cd57b8"

S = "${WORKDIR}/fping-${PV}"

inherit autotools

EXTRA_OECONF = "--enable-ipv4"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
