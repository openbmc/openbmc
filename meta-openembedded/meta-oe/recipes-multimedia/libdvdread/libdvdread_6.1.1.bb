SUMMARY = "DVD access multimeda library"
SECTION = "libs/multimedia"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=64e753fa7d1ca31632bc383da3b57c27"

SRC_URI = "http://download.videolan.org/pub/videolan/libdvdread/${PV}/libdvdread-${PV}.tar.bz2"
SRC_URI[md5sum] = "09c7423568fb679279fd2a2bc6b10b6e"
SRC_URI[sha256sum] = "3e357309a17c5be3731385b9eabda6b7e3fa010f46022a06f104553bf8e21796"

inherit autotools lib_package binconfig pkgconfig

CONFIGUREOPTS_remove = "--disable-silent-rules"
