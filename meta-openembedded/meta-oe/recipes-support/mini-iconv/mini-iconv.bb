# Copyright (C) 2012 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "A minimal iconv implementation w/ support for UTF-8, ASCII, ISO-8859-1"
HOMEPAGE = "http://tinderbox.dev.gentoo.org/portage/local/misc/mini-iconv/"
LICENSE = "MPL-1.1"
SECTION = "libs"
DEPENDS = ""
PROVIDES = "virtual/libiconv"
LIC_FILES_CHKSUM = "file://iconv.c;beginline=1;endline=6;md5=35af9d9924327fe8a0a1fe3a2cb454c8"
SRC_URI = "http://mirror.meleeweb.net/pub/linux/gentoo/distfiles/mini-iconv.tar.bz2"
SRC_URI[md5sum] = "84412221e26505a2b3855d4a1cdcd0e0"
SRC_URI[sha256sum] = "3552262bf1bcf8e859a2a3a7adfb0367af8593383e730c492e981477aac0a0d4"


S = "${WORKDIR}/${BPN}"

do_install() {
    oe_runmake install DESTDIR=${D} PREFIX=${prefix} LIB=${base_libdir}
}
