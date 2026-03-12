SUMMARY = "Highway is a C++ library for SIMD (Single Instruction, Multiple Data)"
HOMEPAGE = "https://github.com/google/highway/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b42edef8fa55315f34f2370b4715ca9"

inherit cmake

SRC_URI = "git://github.com/google/highway.git;protocol=https;branch=master \
           file://0001-Add-cmake-check-for-deducing-32bit-or-64bit-RISCV.patch"

SRCREV = "ac0d5d297b13ab1b89f48484fc7911082d76a93f"

EXTRA_OECMAKE = "-DBUILD_TESTING=0 -DCMAKE_BUILD_TYPE=Release"

CXXFLAGS:append:arm = " -mfp16-format=ieee"
# Option not supported with clang and its default format for __fp16 anyway with clang
CXXFLAGS:remove:toolchain-clang = "-mfp16-format=ieee"

BBCLASSEXTEND = "native"
