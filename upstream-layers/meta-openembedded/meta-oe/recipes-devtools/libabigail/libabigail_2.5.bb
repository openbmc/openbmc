SUMMARY = "The ABI Generic Analysis and Instrumentation Library"
HOMEPAGE = "https://sourceware.org/libabigail/"

LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=0bcd48c3bdfef0c9d9fd17726e4b7dab"

SRC_URI = "http://mirrors.kernel.org/sourceware/${BPN}/${BP}.tar.xz \
           file://0001-Check-for-correct-fts-module.patch \
           "
SRC_URI[sha256sum] = "7cfc4e9b00ae38d87fb0c63beabb32b9cbf9ce410e52ceeb5ad5b3c5beb111f3"

DEPENDS = "elfutils libxml2"
DEPENDS:append:libc-musl = " fts"

inherit autotools pkgconfig lib_package

BBCLASSEXTEND = "native nativesdk"
