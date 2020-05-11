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

SRC_URI[md5sum] = "f9a228619aab2446addc9c9e0e2de149"
SRC_URI[sha256sum] = "705dac7a671b3f440181481e607b0908129a9cf1ddfcba75d66436c0e7d33641"
