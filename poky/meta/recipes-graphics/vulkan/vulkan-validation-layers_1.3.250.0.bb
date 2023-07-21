SUMMARY = "Vulkan Validation layers"
DESCRIPTION = "Khronos official Vulkan validation layers to assist developers \
in verifying that their applications correctly use the Vulkan API"
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/KhronosGroup/Vulkan-ValidationLayers"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8df9e8826734226d08cb412babfa599c"

SRC_URI = "git://git@github.com/KhronosGroup/Vulkan-ValidationLayers.git;branch=sdk-1.3.250;protocol=https \
           file://0001-scripts-CMakeLists.txt-append-to-CMAKE_FIND_ROOT_PAT.patch \
           "
SRCREV = "1541e00a63cd125f15d231d5a8059ebe66503b25"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "vulkan"

DEPENDS = "vulkan-headers vulkan-loader spirv-headers spirv-tools glslang"

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

PACKAGECONFIG[x11] = "-DBUILD_WSI_XLIB_SUPPORT=ON -DBUILD_WSI_XCB_SUPPORT=ON, -DBUILD_WSI_XLIB_SUPPORT=OFF -DBUILD_WSI_XCB_SUPPORT=OFF, libxcb libx11 libxrandr"
PACKAGECONFIG[wayland] = "-DBUILD_WSI_WAYLAND_SUPPORT=ON, -DBUILD_WSI_WAYLAND_SUPPORT=OFF, wayland"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)}"

inherit cmake features_check pkgconfig

FILES:${PN} += "${datadir}/vulkan"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""

# These recipes need to be updated in lockstep with each other:
# glslang, vulkan-headers, vulkan-loader, vulkan-tools,
# vulkan-validation-layers, spirv-headers, spirv-tools
# The tags versions should always be sdk-x.y.z, as this is what
# upstream considers a release.
UPSTREAM_CHECK_GITTAGREGEX = "sdk-(?P<pver>\d+(\.\d+)+)"
