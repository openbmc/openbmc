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

PV = "1.2.0+git${SRCPV}"

SRCREV = "37c5ffe3038d42e9fa9ed232ad2cbca4d8f14681"

SRC_URI = "git://github.com/thkukuk/libnsl \
          "

S = "${WORKDIR}/git"

inherit autotools pkgconfig gettext

BBCLASSEXTEND = "native nativesdk"
