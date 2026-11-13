SUMMARY = "H.265/HEVC video encoder"
DESCRIPTION = "A free software library and application for encoding video streams into the H.265/HEVC format."
HOMEPAGE = "https://github.com/Multicorewareinc/x265"

LICENSE = "GPL-2.0-only"
LICENSE_FLAGS = "commercial"
LIC_FILES_CHKSUM = "file://../COPYING;md5=c9e0427bc58f129f99728c62d4ad4091"

DEPENDS = "nasm-native gnutls zlib libpcre numactl"

SRC_URI = " \
    git://github.com/Multicorewareinc/x265.git;protocol=https;branch=master;tag=${PV} \
    file://0001-fix-dynamicHDR10-undefined-uint8_t.patch;patchdir=.. \
    file://0002-x265-fix-build-with-cmake-4.patch;patchdir=.. \
    file://0003-riscv64-Fix-case-of-STRIDE_SHIFT-macro-invocation.patch;patchdir=.. \
    file://0004-cmake-Link-with-libatomic-when-64-bit-atomics-need-i.patch;patchdir=.. \
"
SRCREV = "e9b88125dc21393b3fd8d68e98083bdfb89778a8"
S = "${UNPACKDIR}/x265-${PV}/source"

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
