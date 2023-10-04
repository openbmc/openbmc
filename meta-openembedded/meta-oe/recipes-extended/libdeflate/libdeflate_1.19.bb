SUMMARY = "libdeflate is a library for fast, whole-buffer DEFLATE-based compression and decompression."
HOMEPAGE = "https://github.com/ebiggers/libdeflate"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=7b6977026437092191e9da699ed9f780"

DEPENDS += "gzip zlib"

SRC_URI = "git://github.com/ebiggers/libdeflate.git;protocol=https;branch=master"

S = "${WORKDIR}/git"
SRCREV = "dd12ff2b36d603dbb7fa8838fe7e7176fcbd4f6f"

inherit cmake pkgconfig

