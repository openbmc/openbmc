require ${BPN}.inc

SRC_URI = "https://mesa.freedesktop.org/archive/mesa-${PV}.tar.xz \
           file://0001-meson.build-check-for-all-linux-host_os-combinations.patch \
           file://0002-meson.build-make-TLS-GLX-optional-again.patch \
           file://0003-Allow-enable-DRI-without-DRI-drivers.patch \
           "

SRC_URI[md5sum] = "07cd8cd79de28ec1a374ee3a06e47789"
SRC_URI[sha256sum] = "72114b16b4a84373b2acda060fe2bb1d45ea2598efab3ef2d44bdeda74f15581"

UPSTREAM_CHECK_GITTAGREGEX = "mesa-(?P<pver>\d+(\.\d+)+)"

#because we cannot rely on the fact that all apps will use pkgconfig,
#make eglplatform.h independent of MESA_EGL_NO_X11_HEADER
do_install_append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'true', 'false', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if defined(MESA_EGL_NO_X11_HEADERS) || ${@bb.utils.contains('PACKAGECONFIG', 'x11', '0', '1', d)}/' ${D}${includedir}/EGL/eglplatform.h
    fi
}
