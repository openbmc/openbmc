SUMMARY = "H.265/HEVC video encoder"
DESCRIPTION = "A free software library and application for encoding video streams into the H.265/HEVC format."
HOMEPAGE = "https://bitbucket.org/multicoreware/x265_git"

LICENSE = "GPL-2.0-only"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://../COPYING;md5=c9e0427bc58f129f99728c62d4ad4091"

DEPENDS = "nasm-native gnutls zlib libpcre numactl"

SRC_URI = " \
    https://download.videolan.org/pub/pub/videolan/${BPN}/${BPN}_${PV}.tar.gz \
    file://0001-json11.cpp-Include-cstdint.patch \
    file://0001-x265-fix-build-with-cmake-4.patch \
"
SRC_URI[sha256sum] = "40b1ea0453e0309f0eba934e0ddf533f8f6295966679e8894e8f1c1c8d5e1210"
S = "${UNPACKDIR}/x265_${PV}/source"

inherit lib_package cmake pkgconfig

EXTRA_OECMAKE += "-DENABLE_PIC=ON -DENABLE_SHARED=ON -DENABLE_CLI=ON"
EXTRA_OECMAKE:append:x86 = " -DENABLE_ASSEMBLY=OFF"
do_generate_toolchain_file:append() {
   echo "set(CMAKE_ASM_NASM_FLAGS -DPIC --debug-prefix-map ${S}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR})" >> ${WORKDIR}/toolchain.cmake
}

PACKAGECONFIG ?= "hdr10plus 10bit"
PACKAGECONFIG[hdr10plus] = "-DENABLE_HDR10_PLUS=ON,-DENABLE_HDR10_PLUS=OFF"
PACKAGECONFIG[10bit] = "-DHIGH_BIT_DEPTH=ON,-DHIGH_BIT_DEPTH=OFF"

FILES:${PN} += "${libdir}/libhdr10plus.so"
FILES:${PN}-dev = "${includedir} ${libdir}/pkgconfig ${libdir}/libx265.so"

