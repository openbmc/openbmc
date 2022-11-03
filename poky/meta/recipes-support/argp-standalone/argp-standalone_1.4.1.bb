# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Glibc hierarchical argument parsing standalone library"
DESCRIPTION = "Standalone version of arguments parsing functions from GLIBC"
HOMEPAGE = "https://github.com/ericonr/argp-standalone"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://argp.h;beginline=1;endline=20;md5=464f2cfb1c35a5123f9e309d7afd79f8"
SECTION = "libs"

SRC_URI = "git://github.com/ericonr/argp-standalone;branch=master;protocol=https \
           file://out_of_tree_build.patch \
          "
SRCREV = "e5fe9ad9e83e6765cf8fa787f903d4c6792338b5"
S = "${WORKDIR}/git"

inherit autotools

CFLAGS += "-fPIC -U__OPTIMIZE__"

DEV_PKG_DEPENDENCY = ""
RDEPENDS:${PN}-staticdev = ""

do_install() {
	install -D -m 0644 ${B}/libargp.a ${D}${libdir}/libargp.a
	install -D -m 0644 ${S}/argp.h ${D}${includedir}/argp.h
}
#
# We will skip parsing for non-musl systems
#
COMPATIBLE_HOST = ".*-musl.*"
