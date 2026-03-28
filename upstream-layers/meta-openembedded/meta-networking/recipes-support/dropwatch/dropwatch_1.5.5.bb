SUMMARY = "Dropwatch is a utility to diagnose where packets are getting dropped"
DESCRIPTION = "\
Dropwatch is a utility to help developers and system administrators to \
diagnose problems in the Linux Networking stack, specifically their \
ability to diagnose where packets are getting dropped."
HOMEPAGE = "https://github.com/nhorman/${BPN}"
SECTION = "net/misc"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

SRC_URI = "git://github.com/nhorman/dropwatch.git;protocol=https;branch=master;tag=v${PV} \
		  file://0001-fix-bug-build-with-sysroot-head-file-instead-of-loca.patch \
"
SRCREV = "10ec0adb9758b86a647b2972932aaa98a7d002a5"


DEPENDS = "binutils libnl libpcap readline"

inherit pkgconfig autotools
