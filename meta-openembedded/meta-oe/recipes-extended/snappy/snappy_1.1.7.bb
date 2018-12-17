#
# Copyright (C) 2014 Wind River Systems, Inc.
# Released under the BSD-3-Clause license (see COPYING.BSD-3 for the terms)
#
SUMMARY = "A compression/decompression library"
DESCRIPTION = "Snappy is a fast data compression and decompression library \
It was designed to be very fast and stable, but not to achieve a high \
compression ratio."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=f62f3080324a97b3159a7a7e61812d0c"

SRC_URI = "https://src.fedoraproject.org/repo/pkgs/snappy/snappy-1.1.7.tar.gz/sha512/32046f532606ba545a4e4825c0c66a19be449f2ca2ff760a6fa170a3603731479a7deadb683546e5f8b5033414c50f4a9a29f6d23b7a41f047e566e69eca7caf/snappy-1.1.7.tar.gz"

SRC_URI[md5sum] = "ee9086291c9ae8deb4dac5e0b85bf54a"
SRC_URI[sha256sum] = "3dfa02e873ff51a11ee02b9ca391807f0c8ea0529a4924afa645fbf97163f9d4"

inherit cmake pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[lzo] = "-DHAVE_LIBLZO2=1,-DHAVE_LIBLZO2=0,lzo,"
TARGET_CFLAGS += "-fPIC"

EXTRA_OECMAKE += '-DBUILD_SHARED_LIBS="ON" \
                  '
