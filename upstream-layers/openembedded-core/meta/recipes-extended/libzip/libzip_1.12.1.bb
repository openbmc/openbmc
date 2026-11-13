SUMMARY = "C library for reading, creating, and modifying zip archives"
DESCRIPTION = "libzip is a C library for reading, creating, and modifying \
zip archives. Files can be added from data buffers, files, or compressed \
data copied directly from other zip archives. Changes made without closing \
the archive can be reverted."
HOMEPAGE = "https://libzip.org/"
BUGTRACKER = "https://github.com/nih-at/libzip/issues"
SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6fcea978dc627f41e0bada0de889ec4"

DEPENDS = "zlib bzip2"

SRC_URI = "https://libzip.org/download/libzip-${PV}.tar.xz"
SRC_URI[sha256sum] = "c40062022a1963c80573d48f6070792297cecd7c6bffe3aefcac0d8b9bee8913"

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
