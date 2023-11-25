SUMMARY = "This library aims to be a friendly, portable C implementation of the AV1 Image File Format"
HOMEPAGE = "https://github.com/AOMediaCodec/libavif"
SECTION = "libs"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c528b75b07425b5c1d2e34de98c397b5"

SRC_URI = "git://github.com/AOMediaCodec/libavif.git;protocol=https;branch=v1.0.x"

S = "${WORKDIR}/git"
SRCREV = "d1c26facaf5a8a97919ceee06814d05d10e25622"

DEPENDS = "dav1d"

inherit cmake

EXTRA_OECMAKE += "-DAVIF_CODEC_DAV1D=ON"
