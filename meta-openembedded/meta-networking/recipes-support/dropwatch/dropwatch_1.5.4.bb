SUMMARY = "Dropwatch is a utility to diagnose where packets are getting dropped"
DESCRIPTION = "\
Dropwatch is a utility to help developers and system administrators to \
diagnose problems in the Linux Networking stack, specifically their \
ability to diagnose where packets are getting dropped."
HOMEPAGE = "https://github.com/nhorman/${BPN}"
SECTION = "net/misc"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "git://github.com/nhorman/dropwatch.git;protocol=https;nobranch=1"
SRCREV = "1e7e487a019a7c02f1f429c4d3a4647fa3787a13"

S = "${WORKDIR}/git"

DEPENDS = "binutils libnl libpcap readline"

inherit pkgconfig autotools
