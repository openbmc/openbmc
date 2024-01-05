LICENSE = "ManishSingh"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd1fb9ee90eead85f7b171807b3ab4f2"

DEPENDS = "libpng libxcursor"

SRC_URI = "http://xorg.freedesktop.org/archive/individual/app/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "32b33ce27b4e285e64ff375731806bb7988cc626ff10915c65f1dc4da640cc9b"

inherit features_check autotools pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

BBCLASSEXTEND = "native"
