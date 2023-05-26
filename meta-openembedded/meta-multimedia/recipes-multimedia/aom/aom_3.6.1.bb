SUMMARY = "Alliance for Open Media - AV1 Codec Library"
DESCRIPTION = "Alliance for Open Media AV1 codec library"

LICENSE = "BSD-2-Clause & AOM-Patent-License-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ea91368c1bbdf877159435572b931f5 \
                    file://PATENTS;md5=a111d47497d3bb49e04eef71377eb8ba \
                   "
SRCREV = "7ade96172b95adc91a5d85bf80c90989cd543ee8"
SRC_URI = "git://aomedia.googlesource.com/aom;protocol=https;branch=main \
           file://0001-subpel_variance_neon-Provide-prototypes-for-missing-.patch"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

DEPENDS = " nasm-native"

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=1 -DENABLE_TESTS=0 \
                 -DPERL_EXECUTABLE=${HOSTTOOLS_DIR}/perl \
                "
CMAKE_VERBOSE = "VERBOSE=1"
CFLAGS:append:libc-musl = " -D_GNU_SOURCE"
EXTRA_OECMAKE:append:arm = " ${@bb.utils.contains("TUNE_FEATURES","neon","-DENABLE_NEON=ON","-DENABLE_NEON=OFF",d)}"

do_generate_toolchain_file:append() {
    echo "set(AOM_AS_FLAGS --debug-prefix-map ${S}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR})" >> ${WORKDIR}/toolchain.cmake
}
