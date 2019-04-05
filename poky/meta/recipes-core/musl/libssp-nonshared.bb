# Copyright (C) 2018 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Minimal libssp_nonshared.a must needed for ssp to work with gcc on musl"
LICENSE = "GPL-3.0-with-GCC-exception"
LIC_FILES_CHKSUM = "file://ssp-local.c;beginline=1;endline=32;md5=c06d391208c0cfcbc541a6728ed65cc4"
SECTION = "libs"

SRC_URI = "file://ssp-local.c"

INHIBIT_DEFAULT_DEPS = "1"

DEPENDS = "virtual/${TARGET_PREFIX}binutils \
           virtual/${TARGET_PREFIX}gcc \
"

do_configure[noexec] = "1"

S = "${WORKDIR}"

do_compile() {
	${CC} ${CPPFLAGS} ${CFLAGS} -fPIE -c ssp-local.c -o ssp-local.o
	${AR} r libssp_nonshared.a ssp-local.o
}
do_install() {
	install -Dm 0644 ${B}/libssp_nonshared.a ${D}${base_libdir}/libssp_nonshared.a
}
#
# We will skip parsing for non-musl systems
#
COMPATIBLE_HOST = ".*-musl.*"
RDEPENDS_${PN}-staticdev = ""
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-staticdev (= ${EXTENDPKGV})"
