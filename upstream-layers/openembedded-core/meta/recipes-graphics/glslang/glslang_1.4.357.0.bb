SUMMARY = "OpenGL / OpenGL ES Reference Compiler"
DESCRIPTION = "Glslang is the official reference compiler front end for the \
OpenGL ES and OpenGL shading languages. It implements a strict interpretation \
of the specifications for these languages. It is open and free for anyone to use, \
either from a command line or programmatically."
SECTION = "graphics"
HOMEPAGE = "https://www.khronos.org/opengles/sdk/tools/Reference-Compiler"
LICENSE = "Apache-2.0 AND BSD-2-Clause AND BSD-3-Clause AND MIT AND GPL-3.0-or-later WITH Bison-exception-2.2"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=50ff9d0fcde2d5b953ebe431c48e34e3"

SRCREV = "168d452a4f460d24b588fed08477a81c44ee27a1"
SRC_URI = "git://github.com/KhronosGroup/glslang.git;protocol=https;branch=main;tag=vulkan-sdk-${PV} \
           file://0001-generate-glslang-pkg-config.patch \
           "
PE = "1"
# These recipes need to be updated in lockstep with each other:
# glslang, vulkan-headers, vulkan-loader, vulkan-tools, spirv-headers, spirv-tools
# vulkan-validation-layers, vulkan-utility-libraries, vulkan-volk.
# The tags versions should always be sdk-x.y.z, as this is what
# upstream considers a release.
UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"

inherit cmake python3native

DEPENDS = "spirv-tools"

EXTRA_OECMAKE = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_SHARED_LIBS=ON \
    -DENABLE_PCH=OFF \
    -DENABLE_CTEST=OFF \
    -DBUILD_EXTERNAL=OFF \
    -DALLOW_EXTERNAL_SPIRV_TOOLS=ON \
"

BBCLASSEXTEND = "native nativesdk"
