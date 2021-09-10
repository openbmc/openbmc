SUMMARY  = "A collection of tools, libraries and tests for shader compilation"
DESCRIPTION = "The Shaderc library provides an API for compiling GLSL/HLSL \
source code to SPIRV modules. It has been shipping in the Android NDK since version r12b."
SECTION = "graphics"
HOMEPAGE = "https://github.com/google/shaderc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRCREV = "031a8c9715df5d7c69dcc1ee9d53cbfa35c7b026"
SRC_URI = "git://github.com/google/shaderc.git;protocol=https;branch=main \
           file://0001-cmake-disable-building-external-dependencies.patch \
           file://0002-libshaderc_util-fix-glslang-header-file-location.patch \
           "
UPSTREAM_CHECK_GITTAGREGEX = "^v(?P<pver>\d+(\.\d+)+)$"
S = "${WORKDIR}/git"

inherit cmake python3native

DEPENDS = "spirv-headers spirv-tools glslang"

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_EXTERNAL=OFF \
    -DSHADERC_SKIP_TESTS=ON \
    -DSHADERC_SKIP_EXAMPLES=ON \
"

BBCLASSEXTEND = "native nativesdk"
