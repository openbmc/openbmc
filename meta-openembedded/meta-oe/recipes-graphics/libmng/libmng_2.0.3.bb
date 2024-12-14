# Copyright (C) 2016 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Development files for the Multiple-image Network Graphics library"
HOMEPAGE = "http://www.libpng.org/pub/mng/"
LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32becdb8930f90eab219a8021130ec09"
SECTION = "devel"
DEPENDS = "zlib"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.tar.gz"

SRC_URI[sha256sum] = "cf112a1fb02f5b1c0fce5cab11ea8243852c139e669c44014125874b14b7dfaa"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/libmng/files/libmng-devel/"
UPSTREAM_CHECK_REGEX = "libmng-devel/(?P<pver>\d+(\.\d+)+)"

inherit autotools-brokensep pkgconfig

PACKAGECONFIG ??= "jpeg"

PACKAGECONFIG[jpeg] = "--with-jpeg,--without-jpeg,jpeg"
PACKAGECONFIG[lcms] = "--with-lcms2,--without-lcms2,lcms"

BBCLASSEXTEND = "native"
