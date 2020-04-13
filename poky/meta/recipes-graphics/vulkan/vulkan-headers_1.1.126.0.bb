SUMMARY = "Vulkan Header files and API registry"
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/KhronosGroup/Vulkan-Headers"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"
SRC_URI = "git://github.com/KhronosGroup/Vulkan-Headers.git;branch=sdk-1.1.126"

SRCREV = "5bc459e2921304c32568b73edaac8d6df5f98b84"

S = "${WORKDIR}/git"

inherit cmake

FILES_${PN} += "${datadir}/vulkan"

UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"
