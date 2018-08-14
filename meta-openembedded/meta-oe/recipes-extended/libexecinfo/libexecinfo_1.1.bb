# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "A quick-n-dirty BSD licensed clone of the GNU libc backtrace facility."
HOMEPAGE = "http://www.freshports.org/devel/libexecinfo"
LIC_FILES_CHKSUM = "file://execinfo.c;endline=25;md5=85bd3fa4ea9acae5182e29db063fe2e5"
LICENSE = "BSD-2-Clause"
SECTION = "libs"
DEPENDS = ""

SRC_URI = "http://distcache.freebsd.org/local-distfiles/itetcu/${BP}.tar.bz2 \
           file://0001-makefile-Fix-build-on-linux.patch \
           file://0002-execinfo-Fix-compiler-errors-found-with-newer-gcc-cl.patch \
           "
SRC_URI[md5sum] = "8e9e81c554c1c5d735bc877448e92b91"
SRC_URI[sha256sum] = "c9a21913e7fdac8ef6b33250b167aa1fc0a7b8a175145e26913a4c19d8a59b1f"

CFLAGS += "-fno-omit-frame-pointer  -D_GNU_SOURCE"

do_install() {
    install -D -m 0744 ${S}/execinfo.h ${D}${includedir}/execinfo.h
    install -D -m 0744 ${S}/stacktraverse.h ${D}${includedir}/stacktraverse.h
    install -D -m 0744 ${B}/libexecinfo.a ${D}${libdir}/libexecinfo.a
    install -D -m 0755 ${B}/libexecinfo.so.1 ${D}${libdir}/libexecinfo.so.1
    ln -s libexecinfo.so.1 ${D}${libdir}/libexecinfo.so
}
#
# We will skip parsing for non-musl systems
#
COMPATIBLE_HOST = ".*-musl.*"

