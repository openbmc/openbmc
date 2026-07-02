SUMMARY = "Vulkan benchmark"
DESCRIPTION = "vkmark is an extensible Vulkan benchmarking suite with targeted, configurable scenes."
HOMEPAGE = "https://github.com/vkmark/vkmark"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING-LGPL2.1;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/vkmark/vkmark.git;protocol=https;branch=master"
SRCREV = "83a128d65b1adddfacb4fa2e555a7e90817a7266"

PV = "2025.01+git${SRCPV}"

inherit meson pkgconfig

DEPENDS = " \
    assimp \
    glm \
    vulkan-loader \
"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xcb', '', d)} wayland kms"

PACKAGECONFIG[xcb] = "-Dxcb=true,-Dxcb=false,libxcb xcb-util-wm"
PACKAGECONFIG[wayland] = "-Dwayland=true,-Dwayland=false,wayland wayland-native wayland-protocols"
PACKAGECONFIG[kms] = "-Dkms=true,-Dkms=false,libdrm virtual/libgbm"

FILES:${PN} += " \
    ${libdir}/vkmark/*.so \
    ${datadir}/vkmark \
    ${mandir}/man1/vkmark.1* \
"
