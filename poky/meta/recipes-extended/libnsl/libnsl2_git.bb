# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Library containing NIS functions using TI-RPC (IPv6 enabled)"
DESCRIPTION = "This library contains the public client interface for NIS(YP) and NIS+\
               it was part of glibc and now is standalone packages. it also supports IPv6"
HOMEPAGE = "https://github.com/thkukuk/libnsl"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"
SECTION = "libs"
DEPENDS = "libtirpc"
DEPENDS_append_libc-musl = " bsd-headers"

PV = "1.0.5+git${SRCPV}"

SRCREV = "dfa2f313524aff9243c4d8ce1bace73786478356"

SRC_URI = "git://github.com/thkukuk/libnsl \
           file://0001-include-sys-cdefs.h-explicitly.patch \
           file://0002-Define-glibc-specific-macros.patch \
           file://0001-nis_call.c-Include-stdint.h-for-uintptr_t-definition.patch \
          "

S = "${WORKDIR}/git"

inherit autotools pkgconfig gettext

EXTRA_OECONF += "--libdir=${libdir}/nsl --includedir=${includedir}/nsl"

do_install_append() {
	install -d ${D}${libdir}
	mv ${D}${libdir}/nsl/pkgconfig ${D}${libdir}
}

FILES_${PN} += "${libdir}/nsl/*.so.*"
FILES_${PN}-dev += "${includedir}/nsl ${libdir}/nsl/*.so"
FILES_${PN}-staticdev += "${libdir}/nsl/*.a"
