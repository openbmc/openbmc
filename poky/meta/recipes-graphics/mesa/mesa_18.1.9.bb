require ${BPN}.inc

SRC_URI = "https://mesa.freedesktop.org/archive/mesa-${PV}.tar.xz \
           file://0001-Simplify-wayland-scanner-lookup.patch \
           file://0002-winsys-svga-drm-Include-sys-types.h.patch \
           file://0003-Properly-get-LLVM-version-when-using-LLVM-Git-releas.patch \
           file://0004-Use-Python-3-to-execute-the-scripts.patch \
           file://0005-dri-i965-Add-missing-time.h-include.patch \
           file://0006-use-PKG_CHECK_VAR-for-defining-WAYLAND_PROTOCOLS_DAT.patch \
"

SRC_URI[md5sum] = "2f8d2098ab478bc3907e42130577b54a"
SRC_URI[sha256sum] = "55f5778d58a710a63d6635f000535768faf7db9e8144dc0f4fd1989f936c1a83"

#because we cannot rely on the fact that all apps will use pkgconfig,
#make eglplatform.h independent of MESA_EGL_NO_X11_HEADER
do_install_append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'true', 'false', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if defined(MESA_EGL_NO_X11_HEADERS) || ${@bb.utils.contains('PACKAGECONFIG', 'x11', '0', '1', d)}/' ${D}${includedir}/EGL/eglplatform.h
    fi
}
