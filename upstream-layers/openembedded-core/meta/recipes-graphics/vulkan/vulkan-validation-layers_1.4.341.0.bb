SUMMARY = "Vulkan Validation layers"
DESCRIPTION = "Khronos official Vulkan validation layers to assist developers \
in verifying that their applications correctly use the Vulkan API"
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/KhronosGroup/Vulkan-ValidationLayers"
SECTION = "libs"

LICENSE = "Apache-2.0 & MIT & BSL-1.0 "
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b1a17d548e004bfbbfaa0c40988b6b31"

SRC_URI = "git://github.com/KhronosGroup/Vulkan-ValidationLayers.git;branch=main;protocol=https;tag=vulkan-sdk-${PV}"
SRCREV = "14de3c0d7b396853d81e6975aad3713984a697a6"

REQUIRED_DISTRO_FEATURES = "vulkan"

DEPENDS = "vulkan-headers vulkan-loader spirv-headers spirv-tools glslang vulkan-utility-libraries"

# BUILD_TESTS            - Not required for OE builds
# USE_ROBIN_HOOD_HASHING - Provides substantial performance improvements on all platforms.
#                          Yocto project doesn't contain a recipe for package so disabled it.
EXTRA_OECMAKE = "\
    -DBUILD_TESTS=OFF \
    -DUSE_ROBIN_HOOD_HASHING=OFF \
    -DGLSLANG_INSTALL_DIR=${STAGING_LIBDIR} \
    -DVULKAN_HEADERS_INSTALL_DIR=${STAGING_EXECPREFIXDIR} \
    -DSPIRV_HEADERS_INSTALL_DIR=${STAGING_EXECPREFIXDIR} \
    "

CXXFLAGS:append = " ${@oe.utils.vartrue('DEBUG_BUILD', '-DXXH_NO_INLINE_HINTS=1', '', d)}"

PACKAGECONFIG[x11] = "-DBUILD_WSI_XLIB_SUPPORT=ON -DBUILD_WSI_XCB_SUPPORT=ON, -DBUILD_WSI_XLIB_SUPPORT=OFF -DBUILD_WSI_XCB_SUPPORT=OFF, libxcb libx11 libxrandr"
PACKAGECONFIG[wayland] = "-DBUILD_WSI_WAYLAND_SUPPORT=ON, -DBUILD_WSI_WAYLAND_SUPPORT=OFF, wayland"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)}"

inherit cmake features_check pkgconfig

FILES:${PN} += "${datadir}/vulkan"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

# These recipes need to be updated in lockstep with each other:
# glslang, vulkan-headers, vulkan-loader, vulkan-tools,
# vulkan-validation-layers, spirv-headers, spirv-tools,
# vulkan-utility-libraries, vulkan-volk.
# The tags versions should always be sdk-x.y.z, as this is what
# upstream considers a release.
UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"
