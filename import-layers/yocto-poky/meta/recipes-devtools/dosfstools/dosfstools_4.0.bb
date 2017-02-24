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
SRC_URI[md5sum] = "20f8388b99702f276c973d228c7cff45"
SRC_URI[sha256sum] = "9037738953559d1efe04fc5408b6846216cc0138f7f9d32de80b6ec3c35e7daf"

UPSTREAM_CHECK_URI = "https://github.com/dosfstools/dosfstools/releases"

inherit autotools pkgconfig

EXTRA_OECONF = "--without-udev --enable-compat-symlinks"

CFLAGS += "-D_GNU_SOURCE ${@bb.utils.contains('DISTRO_FEATURES', 'largefile', '-D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64', '', d)}"

BBCLASSEXTEND = "native"
