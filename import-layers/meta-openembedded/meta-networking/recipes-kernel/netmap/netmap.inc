SUMMARY = "netmap and VALE - very fast packet I/O from userspace (FreeBSD/Linux)"
DESCRIPTION= "NETMAP is a framework for very fast packet I/O from userspace.  VALE is an equally fast in-kernel software switch using the netmap API.  Both are implemented as a single kernel module for FreeBSD and Linux, and can deal with line rate on real or emulated 10 Gbit ports."
SECTION = "networking"
HOMEPAGE = "http://code.google.com/p/netmap/"
LICENSE = "GPLv2+"

LIC_FILES_CHKSUM = "file://README;beginline=13;endline=14;md5=56ae0b9c7ba0476ab9098de94c2714d6"

SRCREV = "da9e19e69b84e4f6f8ae125f8d01b42a4abade6a"
PV = "master+git${SRCPV}"

SRC_URI = "git://github.com/luigirizzo/netmap.git"
SRC_URI += "file://makefile_fixup.patch"

S = "${WORKDIR}/git"

COMPATIBLE_HOST = '(x86_64|i.86|mips|arm).*-linux'
