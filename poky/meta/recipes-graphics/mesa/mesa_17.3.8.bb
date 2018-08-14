require ${BPN}.inc

SRC_URI = "https://mesa.freedesktop.org/archive/mesa-${PV}.tar.xz \
           file://disable-asm-on-non-gcc.patch \
	   file://Use-Python-3-to-execute-the-scripts.patch \
           file://0001-Use-wayland-scanner-in-the-path.patch \
           file://0002-hardware-gloat.patch \
           file://llvm-config-version.patch \
           file://0001-winsys-svga-drm-Include-sys-types.h.patch \
           file://0001-Makefile.vulkan.am-explictly-add-lib-expat-to-intel-.patch \
           file://0001-st-dri-Initialise-modifier-to-INVALID-for-DRI2.patch \
           "

SRC_URI[md5sum] = "203d1a79156ab6926f2d253b377e9d9d"
SRC_URI[sha256sum] = "8f9d9bf281c48e4a8f5228816577263b4c655248dc7666e75034ab422951a6b1"

#because we cannot rely on the fact that all apps will use pkgconfig,
#make eglplatform.h independent of MESA_EGL_NO_X11_HEADER
do_install_append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'true', 'false', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if defined(MESA_EGL_NO_X11_HEADERS) || ${@bb.utils.contains('PACKAGECONFIG', 'x11', '0', '1', d)}/' ${D}${includedir}/EGL/eglplatform.h
    fi
}
