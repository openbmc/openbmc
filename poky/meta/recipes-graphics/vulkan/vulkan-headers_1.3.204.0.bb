SUMMARY = "Vulkan Header files and API registry"
DESCRIPTION = "Vulkan is a 3D graphics and compute API providing cross-platform access \
to modern GPUs with low overhead and targeting realtime graphics applications such as \
games and interactive media. This package contains the development headers \
for packages wanting to make use of Vulkan."
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/KhronosGroup/Vulkan-Headers"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"
SRC_URI = "git://github.com/KhronosGroup/Vulkan-Headers.git;branch=main;protocol=https"

SRCREV = "1dace16d8044758d32736eb59802d171970e9448"

S = "${WORKDIR}/git"

inherit cmake

FILES:${PN} += "${datadir}/vulkan"

UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"
