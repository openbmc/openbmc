require ${BPN}.inc

SRC_URI = "ftp://ftp.freedesktop.org/pub/mesa/${PV}/mesa-${PV}.tar.xz \
           file://replace_glibc_check_with_linux.patch \
           file://clang-compile-PR89599.patch \
           file://disable-asm-on-non-gcc.patch \
"

SRC_URI[md5sum] = "972fd5ad5a63aeabf173fb9adefc6522"
SRC_URI[sha256sum] = "bab24fb79f78c876073527f515ed871fc9c81d816f66c8a0b051d8d653896389"

#because we cannot rely on the fact that all apps will use pkgconfig,
#make eglplatform.h independent of MESA_EGL_NO_X11_HEADER
do_install_append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'egl', 'true', 'false', d)}; then
        sed -i -e 's/^#if defined(MESA_EGL_NO_X11_HEADERS)$/#if defined(MESA_EGL_NO_X11_HEADERS) || ${@bb.utils.contains('PACKAGECONFIG', 'x11', '0', '1', d)}/' ${D}${includedir}/EGL/eglplatform.h
    fi
}
