SUMMARY = "libdeflate is a library for fast, whole-buffer DEFLATE-based compression and decompression."
HOMEPAGE = "https://github.com/ebiggers/libdeflate"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=89f8c510215440fe2f208fba4afe07e0"

DEPENDS += "gzip zlib"

SRC_URI = "git://github.com/ebiggers/libdeflate.git;protocol=https;branch=master;tag=v${PV}"

SRCREV = "92e6a0db9fa848d742f9eb286c92afc60f2c3dda"

inherit cmake pkgconfig
