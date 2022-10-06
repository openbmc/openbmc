SUMMARY = "Alliance for Open Media - AV1 Codec Library"
DESCRIPTION = "Alliance for Open Media AV1 codec library"

LICENSE = "BSD-2-Clause & AOM-Patent-License-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ea91368c1bbdf877159435572b931f5 \
                    file://PATENTS;md5=e69ad12202bd20da3c76a5d3648cfa83 \
                   "

SRC_URI = "git://aomedia.googlesource.com/aom;protocol=https;branch=main \
           file://0001-subpel_variance_neon-Provide-prototypes-for-missing-.patch \
          "

SRCREV = "fd0c9275d36930a6eea6d3c35972e7cf9c512944"

S = "${WORKDIR}/git"

inherit cmake pkgconfig
DEPENDS = " yasm-native"

EXTRA_OECMAKE = " -DBUILD_SHARED_LIBS=1 -DENABLE_TESTS=0 \
                  -DPERL_EXECUTABLE=${HOSTTOOLS_DIR}/perl \
                "

CFLAGS:append:libc-musl = " -D_GNU_SOURCE"
EXTRA_OECMAKE:append:arm = " ${@bb.utils.contains("TUNE_FEATURES","neon","-DENABLE_NEON=ON","-DENABLE_NEON=OFF",d)}"
