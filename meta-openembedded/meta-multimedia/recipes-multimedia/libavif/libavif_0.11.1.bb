SUMMARY = "This library aims to be a friendly, portable C implementation of the AV1 Image File Format"
HOMEPAGE = "https://github.com/AOMediaCodec/libavif"
SECTION = "libs"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c528b75b07425b5c1d2e34de98c397b5"

SRC_URI = "git://github.com/AOMediaCodec/libavif.git;protocol=https;branch=main"

S = "${WORKDIR}/git"
SRCREV = "6ab53189045e7a6fe0bd93d14977b2a4f8efa5e9"

DEPENDS = "dav1d"

inherit cmake

EXTRA_OECMAKE += "-DAVIF_CODEC_DAV1D=ON"
