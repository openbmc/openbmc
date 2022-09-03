# Copyright (C) 2020 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Kronosnet, often referred to as knet, is a network abstraction layer \
           designed for High Availability use cases, where redundancy, security, \
           fault tolerance and fast fail-over are the core requirements of your application."
HOMEPAGE = "https://kronosnet.org/"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING.applications;md5=751419260aa954499f7abaabaa882bbe \
                    file://COPYING.libraries;md5=2d5025d4aa3495befef8f17206a5b0a1"
SECTION = "libs"
DEPENDS = "doxygen-native libqb-native libxml2-native bzip2 libqb libxml2 libnl lksctp-tools lz4 lzo openssl nss xz zlib zstd"

SRCREV = "f8f80fd7f9b85f2626d2c6452612962ad8efca9e"
SRC_URI = "git://github.com/kronosnet/kronosnet;protocol=https;branch=stable1 \
           file://0001-links.c-Fix-build-with-gcc-12.patch \
           "

UPSTREAM_CHECK_URI = "https://github.com/kronosnet/kronosnet/releases"

inherit autotools pkgconfig

S = "${WORKDIR}/git"

# libknet/transport_udp.c:326:48: error: comparison of integers of different signs: 'unsigned long' and 'int' [-Werror,-Wsign-compare]
# for (cmsg = CMSG_FIRSTHDR(&msg);cmsg; cmsg = CMSG_NXTHDR(&msg, cmsg)) {
#                                                             ^~~~~~~~~~~~~~~~~~~~~~~
CFLAGS:append:toolchain-clang = " -Wno-sign-compare"

PACKAGECONFIG[man] = "enable_man="yes", --disable-man, "

PACKAGECONFIG:remove = "man"
