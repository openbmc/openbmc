SUMMARY = "C/C++ Configuration File Library"
DESCRIPTION = "Library for manipulating structured configuration files"
HOMEPAGE = "http://www.hyperrealm.com/libconfig/"
SECTION = "libs"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=fad9b3332be894bab9bc501572864b29"

SRC_URI = "http://www.hyperrealm.com/${BPN}/${BP}.tar.gz"

inherit autotools-brokensep pkgconfig

SRC_URI[md5sum] = "a939c4990d74e6fc1ee62be05716f633"
SRC_URI[sha256sum] = "e31daa390d8e4461c8830512fe2e13ba1a3d6a02a2305a02429eec61e68703f6"
