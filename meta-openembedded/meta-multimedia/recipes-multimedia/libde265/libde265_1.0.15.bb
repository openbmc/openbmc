DESCRIPTION = "libde265 is an open source implementation of the h.265 \
video codec. It is written from scratch and has a plain C API to enable a \
simple integration into other software."
HOMEPAGE = "http://www.libde265.org/"
SECTION = "libs/multimedia"

LICENSE = "LGPL-3.0-only & MIT"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://COPYING;md5=695b556799abb2435c97a113cdca512f"

SRC_URI = "git://github.com/strukturag/libde265.git;branch=master;protocol=https"
SRCREV = "17bb8d9fcea62db8cdeb0fc7ef8d15dbd19a22e4"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

PACKAGECONFIG ?= "libsdl"
PACKAGECONFIG[libsdl] = "-DENABLE_SDL=ON,-DENABLE_SDL=OFF,libsdl2"

FILES:${PN} += "${libdir}/libde265.so"
FILES:${PN}-dev = "${includedir} ${libdir}/cmake ${libdir}/pkgconfig"
