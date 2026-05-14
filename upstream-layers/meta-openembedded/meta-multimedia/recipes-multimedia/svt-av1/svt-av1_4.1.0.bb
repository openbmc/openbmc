SUMMARY = "Scalable Video Technology for AV1 (SVT-AV1 Encoder and Decoder)"
HOMEPAGE = "https://gitlab.com/AOMediaCodec/SVT-AV1"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=87a85a33479e0270481b17d657c1949f"

inherit cmake

DEPENDS = "nasm-native"

SRC_URI = "git://gitlab.com/AOMediaCodec/SVT-AV1.git;protocol=https;nobranch=1;tag=v${PV}"
SRCREV = "c04f951541ad600e0d9c10836f2ab7b9bc69816d"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

