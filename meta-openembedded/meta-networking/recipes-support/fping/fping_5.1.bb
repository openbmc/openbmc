SUMMARY = "sends ICMP ECHO_REQUEST packets to network hosts"
DESCRIPTION = "fping is a ping like program which uses the Internet Control \
Message Protocol (ICMP) echo request to determine if a target host is \
responding. fping differs from ping in that you can specify any number of \
targets on the command line, or specify a file containing the lists of \
targets to ping.  Instead of sending to one target until it times out or \
replies, fping will send out a ping packet and move on to the next target \
in a round-robin fashion."
HOMEPAGE = "http://www.fping.org/"
BUGTRACKER = "https://github.com/schweikert/fping/issues"
SECTION = "net"

LICENSE = "fping"
NO_GENERIC_LICENSE[fping] = "COPYING"
LIC_FILES_CHKSUM = "file://COPYING;md5=c6170fbadddfcd74f011515291d96901"

SRC_URI = "http://www.fping.org/dist/fping-${PV}.tar.gz \
           file://0001-fping-Initialize-msghdr-struct-in-a-portable-way.patch \
           "
SRC_URI[sha256sum] = "1ee5268c063d76646af2b4426052e7d81a42b657e6a77d8e7d3d2e60fd7409fe"

S = "${WORKDIR}/fping-${PV}"

inherit autotools

EXTRA_OECONF = "--enable-ipv4"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
