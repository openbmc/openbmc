SUMMARY = "H.265/HEVC video encoder"
DESCRIPTION = "A free software library and application for encoding video streams into the H.265/HEVC format."
HOMEPAGE = "https://bitbucket.org/multicoreware/x265_git"

LICENSE = "GPL-2.0-only"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://../COPYING;md5=c9e0427bc58f129f99728c62d4ad4091"

DEPENDS = "nasm-native gnutls zlib libpcre numactl"

SRC_URI = "https://bitbucket.org/multicoreware/x265_git/downloads/x265_${PV}.tar.gz"
SRC_URI[sha256sum] = "a31699c6a89806b74b0151e5e6a7df65de4b49050482fe5ebf8a4379d7af8f29"
S = "${WORKDIR}/x265_${PV}/source"

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

