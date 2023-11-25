SUMMARY = "libdvdcss is a simple library for accessing DVDs like block devices"
DESCRIPTION = "libdvdcss is a simple library designed for accessing DVDs like a block device without having to bother about the decryption."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "http://download.videolan.org/pub/libdvdcss/${PV}/libdvdcss-${PV}.tar.bz2"

inherit autotools

EXTRA_OECONF = " --disable-doc "

SRC_URI[sha256sum] = "233cc92f5dc01c5d3a96f5b3582be7d5cee5a35a52d3a08158745d3d86070079"
