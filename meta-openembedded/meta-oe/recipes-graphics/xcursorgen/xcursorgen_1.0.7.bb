LICENSE = "ManishSingh"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd1fb9ee90eead85f7b171807b3ab4f2"

DEPENDS = "libpng libxcursor"

SRC_URI = "http://xorg.freedesktop.org/archive/individual/app/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "25cc7ca1ce5dcbb61c2b471c55e686b5"
SRC_URI[sha256sum] = "35b6f844b24f1776e9006c880a745728800764dbe3b327a128772b4610d8eb3d"

inherit features_check autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

BBCLASSEXTEND = "native"
