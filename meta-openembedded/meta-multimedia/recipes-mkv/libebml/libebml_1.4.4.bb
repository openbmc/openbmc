SUMMARY = "C++ library to parse EBML files"
HOMEPAGE = "https://github.com/Matroska-Org/libebml"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/Matroska-Org/libebml.git;branch=v1.x;protocol=https"
SRCREV = "8330b222fec992b295c8b2149cf70f9ff648ce4a"

S = "${WORKDIR}/git"

inherit pkgconfig cmake dos2unix

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"

