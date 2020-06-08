# dosfstools OE build file
# Copyright (C) 2004-2006, Advanced Micro Devices, Inc.  All Rights Reserved
# Copyright (C) 2015, SÃ¶ren Brinkmann <soeren.brinkmann@gmail>  All Rights Reserved
# Released under the MIT license (see packages/COPYING)
SUMMARY = "DOS FAT Filesystem Utilities"
HOMEPAGE = "https://github.com/dosfstools/dosfstools"

SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://github.com/dosfstools/dosfstools/releases/download/v${PV}/${BP}.tar.xz \
          "
SRC_URI[md5sum] = "07a1050db1a898e9a2e03b0c4569c4bd"
SRC_URI[sha256sum] = "e6b2aca70ccc3fe3687365009dd94a2e18e82b688ed4e260e04b7412471cc173"

UPSTREAM_CHECK_URI = "https://github.com/dosfstools/dosfstools/releases"

inherit autotools pkgconfig

EXTRA_OECONF = "--without-udev --enable-compat-symlinks"

CFLAGS += "-D_GNU_SOURCE -D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64"

BBCLASSEXTEND = "native"

# Add codepage437 to avoid error from `dosfsck -l`
RRECOMMENDS_${PN}_append_libc-glibc = " glibc-gconv-ibm437"
