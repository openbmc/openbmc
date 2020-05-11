# Copyright (C) 2020 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = " Kronosnet, often referred to as knet, is a network abstraction layer designed for High Availability use cases, where redundancy, security, fault tolerance and fast fail-over are the core requirements of your application."
HOMEPAGE = "https://kronosnet.org/"
LICENSE = "GPL-2.0+ & LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING.applications;md5=751419260aa954499f7abaabaa882bbe \
                    file://COPYING.libraries;md5=2d5025d4aa3495befef8f17206a5b0a1"
SECTION = "libs"
DEPENDS = "doxygen-native libqb-native libxml2-native bzip2 libqb libxml2 libnl lksctp-tools lz4 lzo openssl nss xz zlib zstd"

SRCREV = "0ba5985c3ddec8429b989f0e7bd3324f53e0a9b0"
SRC_URI = "git://github.com/kronosnet/kronosnet;protocol=https;branch=stable1"

inherit autotools

S = "${WORKDIR}/git"

# libknet/transport_udp.c:326:48: error: comparison of integers of different signs: 'unsigned long' and 'int' [-Werror,-Wsign-compare]
# for (cmsg = CMSG_FIRSTHDR(&msg);cmsg; cmsg = CMSG_NXTHDR(&msg, cmsg)) {
#                                                             ^~~~~~~~~~~~~~~~~~~~~~~
CFLAGS_append_toolchain-clang = " -Wno-sign-compare"

