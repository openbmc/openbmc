SUMMARY = "libdvdcss is a simple library for accessing DVDs like block devices"
DESCRIPTION = "libdvdcss is a simple library designed for accessing DVDs like a block device without having to bother about the decryption."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://download.videolan.org/pub/libdvdcss/${PV}/libdvdcss-${PV}.tar.xz"
SRC_URI[sha256sum] = "529463e4d1befef82e5c6e470db7661a2db0343e092a2fb0d6c037cab8a5c399"

inherit meson pkgconfig manpages

PACKAGECONFIG[manpages] = "-Denable_docs=true,-Denable_docs=false,doxygen-native"

