SUMMARY = "3D graphics and compute API common loader"
DESCRIPTION = "Vulkan is a new generation graphics and compute API \
that provides efficient access to modern GPUs. These packages \
provide only the common vendor-agnostic library loader, headers and \
the vulkaninfo utility."
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/KhronosGroup/Vulkan-Loader"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7dbefed23242760aa3475ee42801c5ac"
SRC_URI = "git://github.com/KhronosGroup/Vulkan-Loader.git;branch=sdk-1.1.126"
SRCREV = "4adad4ff705fa76f9edb2d37cb57e593decb60ed"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "vulkan"

inherit cmake features_check
ANY_OF_DISTRO_FEATURES = "x11 wayland"

DEPENDS += "vulkan-headers"

EXTRA_OECMAKE = "\
                 -DBUILD_TESTS=OFF \
                 -DPYTHON_EXECUTABLE=${HOSTTOOLS_DIR}/python3 \
                 -DASSEMBLER_WORKS=FALSE \
                 "

# must choose x11 or wayland or both
PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland x11', d)}"

PACKAGECONFIG[x11] = "-DBUILD_WSI_XLIB_SUPPORT=ON -DBUILD_WSI_XCB_SUPPORT=ON, -DBUILD_WSI_XLIB_SUPPORT=OFF -DBUILD_WSI_XCB_SUPPORT=OFF, libxcb libx11 libxrandr"
PACKAGECONFIG[wayland] = "-DBUILD_WSI_WAYLAND_SUPPORT=ON, -DBUILD_WSI_WAYLAND_SUPPORT=OFF, wayland"

RRECOMMENDS_${PN} = "mesa-vulkan-drivers"

UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"
