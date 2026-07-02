SUMMARY = "The ABI Generic Analysis and Instrumentation Library"
HOMEPAGE = "https://sourceware.org/libabigail/"

LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=0bcd48c3bdfef0c9d9fd17726e4b7dab"

SRC_URI = "http://mirrors.kernel.org/sourceware/${BPN}/${BP}.tar.xz \
           "
SRC_URI[sha256sum] = "0cc10e6471398330e001b9fe37f1e8c5108a9ab632b08ca9634d6c64bc380b78"

DEPENDS = "elfutils libxml2 xxhash"
DEPENDS:append:libc-musl = " fts"

inherit autotools pkgconfig lib_package

BBCLASSEXTEND = "native nativesdk"
