LICENSE = "ManishSingh"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd1fb9ee90eead85f7b171807b3ab4f2"

DEPENDS = "libpng libxcursor"

SRC_URI = "http://xorg.freedesktop.org/archive/individual/app/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "0cc9e156ac84ca16ea902710af35e0faffa51d13797071e3b4b6cc7cbd493bbc"

inherit features_check autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

BBCLASSEXTEND = "native"
