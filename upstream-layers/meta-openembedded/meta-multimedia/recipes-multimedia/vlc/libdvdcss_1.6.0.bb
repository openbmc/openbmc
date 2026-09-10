SUMMARY = "libdvdcss is a simple library for accessing DVDs like block devices"
DESCRIPTION = "libdvdcss is a simple library designed for accessing DVDs like a block device without having to bother about the decryption."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://download.videolan.org/pub/libdvdcss/${PV}/libdvdcss-${PV}.tar.xz"
SRC_URI[sha256sum] = "7ea556c846b7bfc32d47b41cae56d1863a6b6d5f706bb162778d6f298490977c"

inherit meson pkgconfig manpages

PACKAGECONFIG[manpages] = "-Denable_docs=true,-Denable_docs=false,doxygen-native"

