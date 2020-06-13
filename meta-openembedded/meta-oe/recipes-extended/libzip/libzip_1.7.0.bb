DESCRIPTION = "libzip is a C library for reading, creating, and modifying zip archives."
HOMEPAGE = "https://libzip.org/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e33bb117aa55f9aad3d28e29256f9919"

DEPENDS = "zlib bzip2"

PACKAGECONFIG[ssl] = "-DENABLE_OPENSSL=ON,-DENABLE_OPENSSL=OFF,openssl"
PACKAGECONFIG[lzma] = "-DENABLE_LZMA=ON,-DENABLE_LZMA=OFF,xz"

PACKAGECONFIG ?= "ssl lzma"

inherit cmake

SRC_URI = "https://libzip.org/download/libzip-${PV}.tar.xz"

SRC_URI[sha256sum] = "d26b2952426d2518f3db5cdeda4fe3cd668fc5bb38a598781e4d1d3f7f8ca7be"
