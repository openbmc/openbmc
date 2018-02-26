# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Glibc hierarchical argument parsing standalone library"
HOMEPAGE = "http://www.lysator.liu.se/~nisse/misc/"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://argp.h;beginline=1;endline=20;md5=008b7e53dea6f9e1d9fdef0d9cf3184a"
SECTION = "libs"

SRC_URI = "http://www.lysator.liu.se/~nisse/misc/argp-standalone-${PV}.tar.gz \
           file://0001-throw-in-funcdef.patch \
           file://0002-isprint.patch \
           file://out_of_tree_build.patch \
          "
SRC_URI[md5sum] = "720704bac078d067111b32444e24ba69"
SRC_URI[sha256sum] = "dec79694da1319acd2238ce95df57f3680fea2482096e483323fddf3d818d8be"

inherit autotools

CFLAGS += "-fPIC -U__OPTIMIZE__"

RDEPENDS_${PN}-dev = ""
RDEPENDS_${PN}-staticdev = ""

do_install() {
	install -D -m 0644 ${B}/libargp.a ${D}${libdir}/libargp.a
	install -D -m 0644 ${S}/argp.h ${D}${includedir}/argp.h
}
#
# We will skip parsing for non-musl systems
#
COMPATIBLE_HOST = ".*-musl.*"
