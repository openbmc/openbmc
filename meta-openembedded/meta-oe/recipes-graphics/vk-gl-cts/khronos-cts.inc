LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "\
	git://github.com/KhronosGroup/VK-GL-CTS.git;protocol=https;name=vk-gl-cts;nobranch=1 \
	git://github.com/google/amber;protocol=https;destsuffix=git/external/amber/src;name=amber;branch=main \
	git://github.com/KhronosGroup/glslang.git;protocol=https;destsuffix=git/external/glslang/src;name=glslang;branch=master \
	git://github.com/KhronosGroup/SPIRV-Headers.git;protocol=https;destsuffix=git/external/spirv-headers/src;name=spirv-headers;branch=master \
	git://github.com/KhronosGroup/SPIRV-Tools.git;protocol=https;destsuffix=git/external/spirv-tools/src;name=spirv-tools;branch=master \
	https://raw.githubusercontent.com/baldurk/renderdoc/v1.1/renderdoc/api/app/renderdoc_app.h;subdir=git/external/renderdoc/src;name=renderdoc \
"

S = "${WORKDIR}/git"

SRCREV_FORMAT = "vk-gl-cts_amber_glslang_spirv-headers_spirv-tools"

S = "${WORKDIR}/git"

inherit pkgconfig cmake features_check

ANY_OF_DISTRO_FEATURES += "opengl wayland"

DEPENDS += "libpng zlib virtual/libgles2"

SRC_URI += "file://0001-Workaround-for-GCC-11-uninit-variable-warnings-946.patch;patchdir=external/amber/src \
            file://0001-Include-limits-header-for-numeric_limits.patch;patchdir=external/vulkancts \
            file://0001-vulkancts.patch \
            file://0001-use-library-sonames-for-linking.patch \
"

SRC_URI:append:libc-musl = "\
	file://fix-musl.patch \
"
DEPENDS:append:libc-musl = " libexecinfo"

SRC_URI:append:toolchain-clang = "\
	file://fix-clang-private-operator.patch \
"

# The best thing for the user to do is to not specify any of the following
# PACKAGECONFIGs (i.e. leave it blank) which tells the project to do its own
# probing and build what it thinks is appropriate.
# However, if you want, you can specify one of the following PACKAGECONFIGs
# to override this behaviour.
PACKAGECONFIG ??= ""
PACKAGECONFIG[surfaceless] = "-DDEQP_TARGET=surfaceless,,,,,wayland x11_egl x11_glx x11_egl_glx"
PACKAGECONFIG[wayland] = "-DDEQP_TARGET=wayland,,wayland,,,surfaceless x11_egl x11_glx x11_egl_glx"
PACKAGECONFIG[x11_egl] = "-DDEQP_TARGET=x11_egl,,virtual/libx11 virtual/egl,,,surfaceless wayland x11_glx x11_egl_glx"
PACKAGECONFIG[x11_glx] = "-DDEQP_TARGET=x11_glx,,virtual/libx11,,,surfaceless wayland x11_egl x11_egl_glx"
PACKAGECONFIG[x11_egl_glx] = "-DDEQP_TARGET=x11_glx,,virtual/libx11 virtual/egl,,,surfaceless wayland x11_egl x11_glx"

python __anonymous() {
    # if the user doesn't specify any PACKAGECONFIG then the cts build system
    # is going to probe the sysroot to try to figure out what to build
    # in this case we try to guess whether the user is building for wayland
    # or x11 and add the required dependencies automatically
    distrofeatures = (d.getVar("DISTRO_FEATURES") or "")
    if not bb.utils.contains_any("PACKAGECONFIG", ["surfaceless", "wayland", "x11_egl", "x11_glx", "x11_egl_glx"], True, False, d):
        if "wayland" in distrofeatures:
            d.appendVar("DEPENDS", " wayland ")
        if "x11" in distrofeatures:
            d.appendVar("DEPENDS", " virtual/libx11 virtual/egl ")
}

CTSDIR = "/usr/lib/${BPN}"

FILES:${PN} += "${CTSDIR}"
