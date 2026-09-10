SUMMARY = "Message Digest functions from BSD systems"
DESCRIPTION = "This library provides message digest functions \
found on BSD systems either on their libc (NetBSD, OpenBSD) or \
libmd (FreeBSD, DragonflyBSD, macOS, Solaris) libraries and \
lacking on others like GNU systems."
HOMEPAGE = "https://www.hadrons.org/software/libmd/"

LICENSE = "BSD-2-Clause AND BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=261bf22602270e2b29c53930d409a3ca"

SRC_URI = "https://archive.hadrons.org/software/libmd/libmd-${PV}.tar.xz \
	   file://run-ptest \
"
SRC_URI[sha256sum] = "ac15ffb8430502fbaccdec66c5a82ee0eab0b0f36220df56710feadfeb13d0a0"

inherit autotools ptest

do_compile_ptest() {
    sed -i "/\$(MAKE) \$(AM_MAKEFLAGS) check-TESTS/d" test/Makefile
    oe_runmake check
}


do_install_ptest() {
    install -d ${D}${PTEST_PATH}/test
    for bin in ${B}/test/*; do
        if [ -x "$bin" ]; then
            ${B}/libtool --mode=install install "$bin" ${D}${PTEST_PATH}/test/$(basename "$bin")
        fi
    done
}

BBCLASSEXTEND = "native nativesdk"
