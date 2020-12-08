SUMMARY = "Vulkan Header files and API registry"
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/KhronosGroup/Vulkan-Headers"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"
# was sdk-1.2.135 branch but it was removed upstream, commit is in master branch though
SRC_URI = "git://github.com/KhronosGroup/Vulkan-Headers.git;branch=master"

SRCREV = "fb7f9c9bcd1d1544ea203a1f3d4253d0e90c5a90"

S = "${WORKDIR}/git"

inherit cmake

FILES_${PN} += "${datadir}/vulkan"

UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"
