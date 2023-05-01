SUMMARY = "libdeflate is a library for fast, whole-buffer DEFLATE-based compression and decompression."
HOMEPAGE = "https://github.com/ebiggers/libdeflate"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=7b6977026437092191e9da699ed9f780"

DEPENDS += "gzip zlib"

SRC_URI = "git://github.com/ebiggers/libdeflate.git;protocol=https;branch=master"

S = "${WORKDIR}/git"
SRCREV = "495fee110ebb48a5eb63b75fd67e42b2955871e2"

inherit cmake pkgconfig

