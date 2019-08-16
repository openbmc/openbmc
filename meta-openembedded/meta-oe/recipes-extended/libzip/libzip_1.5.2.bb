DESCRIPTION = "libzip is a C library for reading, creating, and modifying zip archives."
HOMEPAGE = "https://libzip.org/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01f8b1b8da6403739094396e15b1e722"

DEPENDS = "zlib bzip2"

PACKAGECONFIG[ssl] = "-DENABLE_OPENSSL=ON,-DENABLE_OPENSSL=OFF,openssl"

PACKAGECONFIG ?= "ssl"

inherit cmake

SRC_URI = "https://libzip.org/download/libzip-${PV}.tar.xz"

SRC_URI[md5sum] = "f9dd38d273bcdec5d3d1498fe6684f42"
SRC_URI[sha256sum] = "b3de4d4bd49a01e0cab3507fc163f88e1651695b6b9cb25ad174dbe319d4a3b4"
