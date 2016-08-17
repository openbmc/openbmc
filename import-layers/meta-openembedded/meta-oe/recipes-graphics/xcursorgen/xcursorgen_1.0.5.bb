LICENSE = "ManishSingh"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd1fb9ee90eead85f7b171807b3ab4f2"

DEPENDS = "libpng libxcursor"

SRC_URI = "http://xorg.freedesktop.org/archive/individual/app/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "09f56978a62854534deacc8aa8ff3031"
SRC_URI[sha256sum] = "bc7171a0fa212da866fca2301241630e2009aea8c4ddb75f21b51a31c2e4c581"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"
