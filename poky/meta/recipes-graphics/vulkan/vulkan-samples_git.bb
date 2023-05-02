DESCRIPTION = "The Vulkan Samples is collection of resources to help develop optimized Vulkan applications."
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/KhronosGroup/Vulkan-Samples/issues"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=48aa35cefb768436223a6e7f18dc2a2a"

SRC_URI = "gitsm://github.com/KhronosGroup/Vulkan-Samples.git;branch=main;protocol=https;lfs=0 \
           file://0001-CMakeLists.txt-do-not-hardcode-lib-as-installation-t.patch \
           file://debugfix.patch \
           "

UPSTREAM_CHECK_COMMITS = "1"
SRCREV = "28ca2dad83ceb157386e0708f5151ae89c177ad4"

UPSTREAM_CHECK_GITTAGREGEX = "These are not the releases you're looking for"
S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = 'vulkan'
COMPATIBLE_HOST = "(x86_64|aarch64|mips64|powerpc64|riscv64).*-linux"

inherit cmake features_check

FILES:${PN} += "${datadir}"

#
# There is code to remove the prefix CMAKE_SOURCE_DIR from __FILENAME__ paths
# used for logging with LOGE in the code. We need to make this match the value we use
# in the debug source remapping from CFLAGS
#
EXTRA_OECMAKE += "-DCMAKE_DEBUG_SRCDIR=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/"
# Binaries built with PCH enabled don't appear reproducible, differing results were seen
# from some builds depending on the point the PCH was compiled. Disable it to be
# deterministic
EXTRA_OECMAKE += "-DCMAKE_DISABLE_PRECOMPILE_HEADERS=ON"

# This needs to be specified explicitly to avoid xcb/xlib dependencies
EXTRA_OECMAKE += "-DVKB_WSI_SELECTION=D2D"
