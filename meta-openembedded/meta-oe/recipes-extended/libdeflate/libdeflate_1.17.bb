SUMMARY = "libdeflate is a library for fast, whole-buffer DEFLATE-based compression and decompression."
HOMEPAGE = "https://github.com/ebiggers/libdeflate"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=7b6977026437092191e9da699ed9f780"

DEPENDS += "gzip zlib"

SRC_URI = "git://github.com/ebiggers/libdeflate.git;protocol=https;branch=master"

S = "${WORKDIR}/git"
SRCREV = "bd925ae68e99f65d69f20181cb845aaba5c8f098"

inherit cmake pkgconfig

