SUMMARY = "Vulkan Utilities and Tools"
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/KhronosGroup/Vulkan-Tools"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"
SRC_URI = "git://github.com/KhronosGroup/Vulkan-Tools.git;branch=sdk-1.1.126"
SRCREV = "09695dfc5dbe54f869aeaff8db93bb7bb6a220e0"

S = "${WORKDIR}/git"

inherit cmake features_check
ANY_OF_DISTRO_FEATURES = "x11 wayland"
REQUIRED_DISTRO_FEATURES = "vulkan"

DEPENDS += "vulkan-headers vulkan-loader"

EXTRA_OECMAKE = "\
                 -DBUILD_TESTS=OFF \
                 -DBUILD_CUBE=OFF \
                 -DPYTHON_EXECUTABLE=${HOSTTOOLS_DIR}/python3 \
                 "

# must choose x11 or wayland or both
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland x11', d)}"

PACKAGECONFIG[x11] = "-DBUILD_WSI_XLIB_SUPPORT=ON -DBUILD_WSI_XCB_SUPPORT=ON, -DBUILD_WSI_XLIB_SUPPORT=OFF -DBUILD_WSI_XCB_SUPPORT=OFF, libxcb libx11 libxrandr"
PACKAGECONFIG[wayland] = "-DBUILD_WSI_WAYLAND_SUPPORT=ON, -DBUILD_WSI_WAYLAND_SUPPORT=OFF, wayland"

UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"
