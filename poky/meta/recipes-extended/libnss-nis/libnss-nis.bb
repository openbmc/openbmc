# Copyright (C) 2018 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "NSS module for glibc, to provide NIS support for glibc"

DESCRIPTION = "This package contains the NSS NIS plugin for glibc.\
This code was formerly part of glibc, but is now standalone to\
be able to link against TI-RPC for IPv6 support."

HOMEPAGE = "https://github.com/thkukuk/libnss_nis"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"
SECTION = "libs"
DEPENDS += "libtirpc libnsl2"

PV = "3.0+git${SRCPV}"

SRCREV = "d4aea48657a8e90d7922574b8021ee03915a36cb"

SRC_URI = "git://github.com/thkukuk/libnss_nis \
          "

S = "${WORKDIR}/git"

inherit autotools pkgconfig

BBCLASSEXTEND += "native nativesdk"
#
# We will skip parsing this packagegeoup for non-glibc systems
#
COMPATIBLE_HOST_libc-musl = 'null'
