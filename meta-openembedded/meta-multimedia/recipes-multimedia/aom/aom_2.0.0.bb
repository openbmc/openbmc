SUMMARY = "Alliance for Open Media - AV1 Codec Library"
DESCRIPTION = "Alliance for Open Media AV1 codec library"

LICENSE = "BSD-2-Clause & AOM-Patent-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ea91368c1bbdf877159435572b931f5 \
                    file://PATENTS;md5=e69ad12202bd20da3c76a5d3648cfa83 \
                   "

SRC_URI = "git://aomedia.googlesource.com/aom;protocol=https"

SRCREV = "d1d1226af626a61f7ca664b270dd473b92228984"

S = "${WORKDIR}/git"

inherit cmake pkgconfig
DEPENDS = " yasm-native"

EXTRA_OECMAKE = " -DBUILD_SHARED_LIBS=1 -DENABLE_TESTS=0 \
                  -DPERL_EXECUTABLE=${HOSTTOOLS_DIR}/perl \
                "

EXTRA_OECMAKE_append_arm = " ${@bb.utils.contains("TUNE_FEATURES","neon","-DENABLE_NEON=ON","-DENABLE_NEON=OFF",d)}"
