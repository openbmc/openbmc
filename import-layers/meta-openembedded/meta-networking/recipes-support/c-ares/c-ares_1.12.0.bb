# Copyright (c) 2012-2014 LG Electronics, Inc.

SUMMARY = "c-ares is a C library that resolves names asynchronously."
HOMEPAGE = "http://daniel.haxx.se/projects/c-ares/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=f4b026880834eb01c035c5e5cb47ccac"

SRC_URI = "http://c-ares.haxx.se/download/${BP}.tar.gz \
           file://0001-configure.ac-don-t-override-passed-cflags.patch \
"
SRC_URI[md5sum] = "2ca44be1715cd2c5666a165d35788424"
SRC_URI[sha256sum] = "8692f9403cdcdf936130e045c84021665118ee9bfea905d1a76f04d4e6f365fb"

inherit autotools pkgconfig
