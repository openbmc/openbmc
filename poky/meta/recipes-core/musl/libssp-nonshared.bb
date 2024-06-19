# Copyright (C) 2018 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Minimal libssp_nonshared.a must needed for ssp to work with gcc on musl"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://stack_chk.c;beginline=1;endline=30;md5=97e59d9deee678a9332c9ddb2ab6360d"
SECTION = "libs"

# Sourced from https://github.com/intel/linux-sgx/blob/master/sdk/compiler-rt/stack_chk.c
SRC_URI = "file://stack_chk.c"

INHIBIT_DEFAULT_DEPS = "1"

DEPENDS = "virtual/${TARGET_PREFIX}binutils \
           virtual/${TARGET_PREFIX}gcc \
"

do_configure[noexec] = "1"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_compile() {
	${CC} ${CPPFLAGS} ${CFLAGS} -fPIE -c stack_chk.c -o stack_chk.o
	${AR} r libssp_nonshared.a stack_chk.o
}
do_install() {
	install -Dm 0644 ${B}/libssp_nonshared.a ${D}${base_libdir}/libssp_nonshared.a
}
#
# We will skip parsing for non-musl systems
#
COMPATIBLE_HOST = ".*-musl.*"
RDEPENDS:${PN}-staticdev = ""
DEV_PKG_DEPENDENCY = ""
RRECOMMENDS:${PN}-dbg = "${PN}-staticdev (= ${EXTENDPKGV})"
