DESCRIPTION = "libdvdcss is a simple library designed for accessing DVDs like a block device without having to bother about the decryption."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://download.videolan.org/pub/libdvdcss/${PV}/libdvdcss-${PV}.tar.bz2"

inherit autotools

EXTRA_OECONF = " --disable-doc "

SRC_URI[md5sum] = "7f0fdb3ff91d638f5e45ed7536f7eb67"
SRC_URI[sha256sum] = "7c414acd520c4e4dd7267952f72d738ff50321a7869af4d75c65aefad44f1395"
