SUMMARY = "Library for obtaining the call-chain of a program"
DESCRIPTION = "a portable and efficient C programming interface (API) to determine the call-chain of a program"
HOMEPAGE = "http://www.nongnu.org/libunwind"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;beginline=3;md5=3fced11d6df719b47505837a51c16ae5"
DEPENDS += "libatomic-ops"

inherit autotools multilib_header

PACKAGECONFIG ??= ""
PACKAGECONFIG[lzma] = "--enable-minidebuginfo,--disable-minidebuginfo,xz"
PACKAGECONFIG[latexdocs] = "--enable-documentation, --disable-documentation, latex2man-native"

EXTRA_OECONF_arm = "--enable-debug-frame"
EXTRA_OECONF_aarch64 = "--enable-debug-frame"

do_install_append () {
	oe_multilib_header libunwind.h
}

BBCLASSEXTEND = "native"
