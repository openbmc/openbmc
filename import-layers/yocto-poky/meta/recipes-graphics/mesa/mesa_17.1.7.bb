require ${BPN}.inc

SRC_URI = "https://mesa.freedesktop.org/archive/mesa-${PV}.tar.xz \
           file://replace_glibc_check_with_linux.patch \
           file://disable-asm-on-non-gcc.patch \
           file://0001-Use-wayland-scanner-in-the-path.patch \
           file://0002-hardware-gloat.patch \
           file://vulkan-mkdir.patch \
           file://llvm-config-version.patch \
           file://0001-ac-fix-build-after-LLVM-5.0-SVN-r300718.patch \
           file://0002-gallivm-Fix-build-against-LLVM-SVN-r302589.patch \
           file://0001-winsys-svga-drm-Include-sys-types.h.patch \
           file://0001-configure.ac-Always-check-for-expat.patch \
           file://0001-Makefile.vulkan.am-explictly-add-lib-expat-to-intel-.patch \
           "
SRC_URI[md5sum] = "e40bb428a263bd28cbf6478dae45b207"
SRC_URI[sha256sum] = "69f472a874b1122404fa0bd13e2d6bf87eb3b9ad9c21d2f39872a96d83d9e5f5"

#because we cannot rely on the fact that all apps will use pkgconfig,
#make eglplatform.h independent of MESA_EGL_NO_X11_HEADER
do_install_append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'true', 'false', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if defined(MESA_EGL_NO_X11_HEADERS) || ${@bb.utils.contains('PACKAGECONFIG', 'x11', '0', '1', d)}/' ${D}${includedir}/EGL/eglplatform.h
    fi
}
