SUMMARY = "C++ library to parse and create Matroska files(.mkv and .mka)"
HOMEPAGE = "https://github.com/Matroska-Org/libmatroska"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libebml"

SRC_URI = "git://github.com/Matroska-Org/libmatroska.git;branch=v1.x;protocol=https"
SRCREV = "f5315fddda2d434e47035c038549a808d8b8eac7"

S = "${WORKDIR}/git"

inherit pkgconfig cmake

#Static library enabled by default. It has been added in case you want to use it dynamically.
#EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"
