SUMMARY = "Vulkan Utility Libraries"
DESCRIPTION = "Common libraries created to share code across various \
Vulkan repositories, solving long standing issues for Vulkan SDK \
developers and users."
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/KhronosGroup/Vulkan-Utility-Libraries"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=4ca2d6799091aaa98a8520f1b793939b"

SRC_URI = "git://github.com/KhronosGroup/Vulkan-Utility-Libraries.git;branch=main;protocol=https"
SRCREV = "4cfc176e3242b4dbdfd3f6c5680c5d8f2cb7db45"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "vulkan"

DEPENDS = "vulkan-headers"

EXTRA_OECMAKE = "\
    -DBUILD_TESTS=OFF \
    "

inherit cmake features_check pkgconfig

# These recipes need to be updated in lockstep with each other:
# glslang, vulkan-headers, vulkan-loader, vulkan-tools,
# vulkan-validation-layers, spirv-headers, spirv-tools,
# vulkan-utility-libraries.
# The tags versions should always be sdk-x.y.z, as this is what
# upstream considers a release.
UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"
