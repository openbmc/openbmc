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

SRC_URI = "gitsm://github.com/google/snappy.git;protocol=https;branch=main"
SRCREV = "6af9287fbdb913f0794d0148c6aa43b58e63c8e3"

inherit cmake pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[lzo] = "-DHAVE_LIBLZO2=1,-DHAVE_LIBLZO2=0,lzo,"
TARGET_CFLAGS += "-fPIC"

EXTRA_OECMAKE += '-DBUILD_SHARED_LIBS="ON" -DSNAPPY_BUILD_TESTS="OFF" -DSNAPPY_BUILD_BENCHMARKS="OFF"'

CVE_PRODUCT = "google:snappy"
