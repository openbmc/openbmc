SUMMARY = "Highway is a C++ library for SIMD (Single Instruction, Multiple Data)"
HOMEPAGE = "https://github.com/google/highway/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2b42edef8fa55315f34f2370b4715ca9"

inherit cmake

SRC_URI = "git://github.com/google/highway.git;protocol=https;branch=master"

SRCREV = "457c891775a7397bdb0376bb1031e6e027af1c48"
S = "${WORKDIR}/git"

EXTRA_OECMAKE = "-DBUILD_TESTING=0 -DCMAKE_BUILD_TYPE=Release"
CXXFLAGS:append:arm = " -mfp16-format=ieee"
# Option not supported with clang and its default format for __fp16 anyway with clang
CXXFLAGS:remove:toolchain-clang = "-mfp16-format=ieee"
