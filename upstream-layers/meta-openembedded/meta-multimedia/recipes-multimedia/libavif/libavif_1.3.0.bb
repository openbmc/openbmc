SUMMARY = "This library aims to be a friendly, portable C implementation of the AV1 Image File Format"
HOMEPAGE = "https://github.com/AOMediaCodec/libavif"
SECTION = "libs"
# Most is the code is under BSD-2, but libyuv is under BSD-3, and iccjpeg is under IJG
LICENSE = "BSD-2-Clause & BSD-3-Clause & IJG"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51549db0941829faeedcc86efec2f4c0"

SRC_URI = "git://github.com/AOMediaCodec/libavif.git;protocol=https;branch=main;tag=v${PV}"

SRCREV = "1aadfad932c98c069a1204261b1856f81f3bc199"

DEPENDS = "dav1d"

inherit cmake

EXTRA_OECMAKE += "-DAVIF_CODEC_DAV1D=ON -DAVIF_LIBYUV=OFF"
