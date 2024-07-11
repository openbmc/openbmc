SUMMARY = "libdeflate is a library for fast, whole-buffer DEFLATE-based compression and decompression."
HOMEPAGE = "https://github.com/ebiggers/libdeflate"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=7b6977026437092191e9da699ed9f780"

DEPENDS += "gzip zlib"

SRC_URI = "git://github.com/ebiggers/libdeflate.git;protocol=https;branch=master \
    file://0001-lib-arm-don-t-use-explicit-armv8.2-a-on-gcc-13.2-and.patch \
"

S = "${WORKDIR}/git"
SRCREV = "275aa5141db6eda3587214e0f1d3a134768f557d"

inherit cmake pkgconfig
