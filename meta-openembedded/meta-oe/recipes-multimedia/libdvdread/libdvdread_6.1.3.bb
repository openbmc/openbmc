SUMMARY = "DVD access multimeda library"
SECTION = "libs/multimedia"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=64e753fa7d1ca31632bc383da3b57c27"

SRC_URI = "http://download.videolan.org/pub/videolan/libdvdread/${PV}/libdvdread-${PV}.tar.bz2"
SRC_URI[sha256sum] = "ce35454997a208cbe50e91232f0e73fb1ac3471965813a13b8730a8f18a15369"

inherit autotools lib_package binconfig pkgconfig

CONFIGUREOPTS:remove = "--disable-silent-rules"
