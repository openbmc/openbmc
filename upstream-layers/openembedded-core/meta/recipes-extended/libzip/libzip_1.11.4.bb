SUMMARY = "C library for reading, creating, and modifying zip archives"
DESCRIPTION = "libzip is a C library for reading, creating, and modifying \
zip archives. Files can be added from data buffers, files, or compressed \
data copied directly from other zip archives. Changes made without closing \
the archive can be reverted."
HOMEPAGE = "https://libzip.org/"
BUGTRACKER = "https://github.com/nih-at/libzip/issues"
SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8a9d2078f35e61cf1122ccd440687cf"

DEPENDS = "zlib bzip2"

SRC_URI = "https://libzip.org/download/libzip-${PV}.tar.xz"
SRC_URI[sha256sum] = "8a247f57d1e3e6f6d11413b12a6f28a9d388de110adc0ec608d893180ed7097b"

inherit cmake

PACKAGECONFIG ?= "ssl lzma tools examples"

PACKAGECONFIG[ssl] = "-DENABLE_OPENSSL=ON,-DENABLE_OPENSSL=OFF,openssl"
PACKAGECONFIG[gnutls] = "-DENABLE_GNUTLS=ON,-DENABLE_GNUTLS=OFF,gnutls nettle"
PACKAGECONFIG[lzma] = "-DENABLE_LZMA=ON,-DENABLE_LZMA=OFF,xz"
PACKAGECONFIG[zstd] = "-DENABLE_ZSTD=ON,-DENABLE_ZSTD=OFF,zstd"
PACKAGECONFIG[tools] = "-DBUILD_TOOLS=ON,-DBUILD_TOOLS=OFF"
PACKAGECONFIG[examples] = "-DBUILD_EXAMPLES=ON,-DBUILD_EXAMPLES=OFF"
PACKAGECONFIG[tests] = "-DBUILD_REGRESS=ON,-DBUILD_REGRESS=OFF"

BBCLASSEXTEND = "native nativesdk"
