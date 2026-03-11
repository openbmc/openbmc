SUMMARY = "The Vulkan Samples is collection of resources to help develop optimized Vulkan applications."
HOMEPAGE = "https://www.khronos.org/vulkan/"
BUGTRACKER = "https://github.com/KhronosGroup/Vulkan-Samples/issues"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=48aa35cefb768436223a6e7f18dc2a2a"

SRC_URI = "gitsm://github.com/KhronosGroup/Vulkan-Samples.git;branch=main;protocol=https;lfs=0 \
           file://0001-SPIRV-SpvBuilder.h-add-missing-cstdint-include.patch;patchdir=third_party/glslang \
           "

UPSTREAM_CHECK_COMMITS = "1"
SRCREV = "d27205d14d01ea7d33efc8ba2862478612370182"

UPSTREAM_CHECK_GITTAGREGEX = "These are not the releases you're looking for"

REQUIRED_DISTRO_FEATURES = 'vulkan'

# Avoid erroring on including <ciso646> instead of <version> in c++17
CXXFLAGS += "-Wno-error=cpp"
inherit cmake features_check

FILES:${PN} += "${datadir}"

# Binaries built with PCH enabled don't appear reproducible, differing results were seen
# from some builds depending on the point the PCH was compiled. Disable it to be
# deterministic
EXTRA_OECMAKE += "-DCMAKE_DISABLE_PRECOMPILE_HEADERS=ON"

# This needs to be specified explicitly to avoid xcb/xlib dependencies
EXTRA_OECMAKE += "-DVKB_WSI_SELECTION=D2D"

# Clang is fussy about incompatible options on aarch64/x86_64
# x86_64-poky-linux-clang++: error: overriding '-ffp-model=precise' option with '-ffp-contract=fast' [-Werror,-Woverriding-option]
CXXFLAGS:append:toolchain-clang = " -Wno-error=overriding-option"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
