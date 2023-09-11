LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "\
	git://github.com/KhronosGroup/VK-GL-CTS.git;protocol=https;name=vk-gl-cts;nobranch=1 \
	git://github.com/google/amber;protocol=https;destsuffix=git/external/amber/src;name=amber;branch=main \
	git://github.com/KhronosGroup/glslang.git;protocol=https;destsuffix=git/external/glslang/src;name=glslang;branch=main \
	git://github.com/KhronosGroup/SPIRV-Headers.git;protocol=https;destsuffix=git/external/spirv-headers/src;name=spirv-headers;branch=main \
	git://github.com/KhronosGroup/SPIRV-Tools.git;protocol=https;destsuffix=git/external/spirv-tools/src;name=spirv-tools;branch=main \
        git://github.com/open-source-parsers/jsoncpp.git;protocol=https;destsuffix=git/external/jsoncpp/src;name=jsoncpp;branch=master \
        git://github.com/KhronosGroup/Vulkan-Docs.git;protocol=https;destsuffix=git/external/vulkan-docs/src;name=vulkan-docs;branch=main \
        https://raw.githubusercontent.com/baldurk/renderdoc/v1.1/renderdoc/api/app/renderdoc_app.h;subdir=git/external/renderdoc/src;name=renderdoc \
        git://github.com/Igalia/ESExtractor.git;protocol=https;destsuffix=git/external/ESExtractor/src;name=ESExtractor;branch=main \
        git://github.com/nvpro-samples/vk_video_samples.git;protocol=https;destsuffix=git/external/video-parser/src;name=video-parser;branch=main \
        file://gen-framework-path.patch \
"

S = "${WORKDIR}/git"

SRCREV_FORMAT = "vk-gl-cts_amber_glslang_spirv-headers_spirv-tools_jsoncpp_vulkan-docs"

S = "${WORKDIR}/git"

inherit pkgconfig cmake features_check python3native qemu

ANY_OF_DISTRO_FEATURES += "opengl vulkan"

DEPENDS += "python3-lxml-native libpng zlib virtual/libgles2 qemu-native"

SRC_URI += " \
            file://0001-vulkancts-Include-missing-cstdint.patch \
            file://0001-use-library-sonames-for-linking.patch \
"

SRC_URI:append:libc-musl = "\
	file://fix-musl.patch \
"
DEPENDS:append:libc-musl = " libexecinfo"

SRC_URI:append:toolchain-clang = "\
	file://fix-clang-private-operator.patch \
"

EXTRA_OECMAKE:prepend:class-target = "-DCMAKE_CROSSCOMPILING_EMULATOR=${WORKDIR}/qemuwrapper -DWAYLAND_SCANNER=${STAGING_BINDIR_NATIVE}/wayland-scanner -DWAYLAND_PROTOCOLS_DIR=${STAGING_DATADIR}/wayland-protocols"

# The best thing for the user to do is to not specify any of the following
# PACKAGECONFIGs (i.e. leave it blank) which tells the project to do its own
# probing and build what it thinks is appropriate.
# However, if you want, you can specify one of the following PACKAGECONFIGs
# to override this behaviour.
PACKAGECONFIG ??= ""
PACKAGECONFIG[surfaceless] = "-DDEQP_TARGET=surfaceless,,,,,wayland x11_egl x11_glx x11_egl_glx"
PACKAGECONFIG[wayland] = "-DDEQP_TARGET=wayland,,wayland-native wayland wayland-protocols,,,surfaceless x11_egl x11_glx x11_egl_glx"
PACKAGECONFIG[x11_egl] = "-DDEQP_TARGET=x11_egl,,virtual/libx11 virtual/egl,,,surfaceless wayland x11_glx x11_egl_glx"
PACKAGECONFIG[x11_glx] = "-DDEQP_TARGET=x11_glx,,virtual/libx11,,,surfaceless wayland x11_egl x11_egl_glx"
PACKAGECONFIG[x11_egl_glx] = "-DDEQP_TARGET=x11_glx,,virtual/libx11 virtual/egl,,,surfaceless wayland x11_egl x11_glx"

do_configure:append() {
        # Write out a qemu wrapper that will be used by cmake
        # so that it can run target helper binaries through that.
        qemu_binary="${@qemu_wrapper_cmdline(d, d.getVar('STAGING_DIR_HOST'), [d.expand('${STAGING_DIR_HOST}${libdir}'),d.expand('${STAGING_DIR_HOST}${base_libdir}')])}"
        cat > ${WORKDIR}/qemuwrapper << EOF
#!/bin/sh
$qemu_binary "\$@"
EOF
        chmod +x ${WORKDIR}/qemuwrapper
}

python __anonymous() {
    # if the user doesn't specify any PACKAGECONFIG then the cts build system
    # is going to probe the sysroot to try to figure out what to build
    # in this case we try to guess whether the user is building for wayland
    # or x11 and add the required dependencies automatically
    distrofeatures = (d.getVar("DISTRO_FEATURES") or "")
    if not bb.utils.contains_any("PACKAGECONFIG", ["surfaceless", "wayland", "x11_egl", "x11_glx", "x11_egl_glx"], True, False, d):
        if "wayland" in distrofeatures:
            d.appendVar("DEPENDS", " wayland-native ${MLPREFIX}wayland ${MLPREFIX}wayland-protocols")
        if "x11" in distrofeatures:
            d.appendVar("DEPENDS", " virtual/${MLPREFIX}libx11 virtual/${MLPREFIX}egl ")
}

CTSDIR = "/usr/lib/${BPN}"

FILES:${PN} += "${CTSDIR}"
