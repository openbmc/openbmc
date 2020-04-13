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

SRC_URI = "https://src.fedoraproject.org/repo/pkgs/snappy/snappy-1.1.8.tar.gz/sha512/efe18ff1b3edda1b4b6cefcbc6da8119c05d63afdbf7a784f3490353c74dced76baed7b5f1aa34b99899729192b9d657c33c76de4b507a51553fa8001ae75c1c/snappy-1.1.8.tar.gz"

SRC_URI[md5sum] = "70e48cba7fecf289153d009791c9977f"
SRC_URI[sha256sum] = "16b677f07832a612b0836178db7f374e414f94657c138e6993cbfc5dcc58651f"

inherit cmake pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[lzo] = "-DHAVE_LIBLZO2=1,-DHAVE_LIBLZO2=0,lzo,"
TARGET_CFLAGS += "-fPIC"

EXTRA_OECMAKE += '-DBUILD_SHARED_LIBS="ON" \
                  '
