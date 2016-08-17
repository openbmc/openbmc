# Copyright (c) 2012-2014 LG Electronics, Inc.

DESCRIPTION = "c-ares is a C library that resolves names asynchronously."
HOMEPAGE = "http://daniel.haxx.se/projects/c-ares/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://ares_init.c;beginline=1;endline=3;md5=53f5ecf4c22c37cf1ddd1ef8f8eccce0"

SRC_URI = "http://c-ares.haxx.se/download/${BP}.tar.gz"
SRC_URI[md5sum] = "d5c6d522cfc54bb6f215a0b7912d46be"
SRC_URI[sha256sum] = "b3612e6617d9682928a1d50c1040de4db6519f977f0b25d40cf1b632900b3efd"

inherit autotools pkgconfig
