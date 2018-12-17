DESCRIPTION = "Links is graphics and text mode WWW \
browser, similar to Lynx."
HOMEPAGE = "http://links.twibright.com/"
SECTION = "console/network"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b0c80473f97008e42e29a9f80fcc55ff"
DEPENDS = "jpeg libpng flex openssl zlib"

SRC_URI = "http://links.twibright.com/download/links-${PV}.tar.bz2 \
           file://ac-prog-cxx.patch \
"

PACKAGECONFIG ??= ""
PACKAGECONFIG[bzip2] = "--with-bzip2,--without-bzip2,bzip2"
PACKAGECONFIG[lzma] = "--with-lzma,--without-lzma,xz"

inherit autotools pkgconfig
