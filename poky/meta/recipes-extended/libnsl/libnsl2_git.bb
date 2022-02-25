# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Library containing NIS functions using TI-RPC (IPv6 enabled)"
DESCRIPTION = "This library contains the public client interface for NIS(YP) and NIS+\
               it was part of glibc and now is standalone packages. it also supports IPv6"
HOMEPAGE = "https://github.com/thkukuk/libnsl"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"
SECTION = "libs"
DEPENDS = "libtirpc"

PV = "2.0.0"

SRCREV = "82245c0c58add79a8e34ab0917358217a70e5100"

SRC_URI = "git://github.com/thkukuk/libnsl;branch=master;protocol=https \
          "

S = "${WORKDIR}/git"

inherit autotools pkgconfig gettext

BBCLASSEXTEND = "native nativesdk"
