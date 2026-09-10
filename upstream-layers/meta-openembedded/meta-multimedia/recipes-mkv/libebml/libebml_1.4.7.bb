SUMMARY = "C++ library to parse EBML files"
HOMEPAGE = "https://github.com/Matroska-Org/libebml"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL;md5=3254c7b4d4712a396bd036bfb211a908"

SRC_URI = "git://github.com/Matroska-Org/libebml.git;branch=v1.x;protocol=https"

SRCREV = "3e72a85de45cbb385d225a0a60eb3c9114743dd8"

DEPENDS = "utfcpp"

inherit pkgconfig cmake dos2unix

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"

