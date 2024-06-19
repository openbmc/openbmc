SUMMARY = "XWayland is an X Server that runs under Wayland."
DESCRIPTION = "XWayland is an X Server running as a Wayland client, \
and thus is capable of displaying native X11 client applications in a \
Wayland compositor environment. The goal of XWayland is to facilitate \
the transition from X Window System to Wayland environments, providing \
a way to run unported applications in the meantime."
HOMEPAGE = "https://fedoraproject.org/wiki/Changes/XwaylandStandalone"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=5df87950af51ac2c5822094553ea1880"

SRC_URI = "https://www.x.org/archive/individual/xserver/xwayland-${PV}.tar.xz"
SRC_URI[sha256sum] = "bef21c4f18807a4ed571c4e2df60ab63b5466bbd502ecceb2485b892ab76dcc2"

UPSTREAM_CHECK_REGEX = "xwayland-(?P<pver>\d+(\.(?!90\d)\d+)+)\.tar"

inherit meson features_check pkgconfig
REQUIRED_DISTRO_FEATURES = "x11 opengl"

DEPENDS += "xorgproto xtrans pixman libxkbfile libxfont2 wayland wayland-native wayland-protocols libdrm libepoxy libxcvt"

OPENGL_PKGCONFIGS = "glx glamor dri3"
PACKAGECONFIG ??= "${XORG_CRYPTO} ${XWAYLAND_EI} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', '${OPENGL_PKGCONFIGS}', '', d)} \
"
PACKAGECONFIG[dri3] = "-Ddri3=true,-Ddri3=false,libxshmfence"
PACKAGECONFIG[libdecor] = "-Dlibdecor=true,-Dlibdecor=false,libdecor"
PACKAGECONFIG[glx] = "-Dglx=true,-Dglx=false,virtual/libgl virtual/libx11"
PACKAGECONFIG[glamor] = "-Dglamor=true,-Dglamor=false,libepoxy virtual/libgbm,libegl"
PACKAGECONFIG[unwind] = "-Dlibunwind=true,-Dlibunwind=false,libunwind"
PACKAGECONFIG[xinerama] = "-Dxinerama=true,-Dxinerama=false"

# Xorg requires a SHA1 implementation, pick one
XORG_CRYPTO ??= "openssl"
PACKAGECONFIG[openssl] = "-Dsha1=libcrypto,,openssl"
PACKAGECONFIG[nettle] = "-Dsha1=libnettle,,nettle"
PACKAGECONFIG[gcrypt] = "-Dsha1=libgcrypt,,libgcrypt"
XWAYLAND_EI ??= "xwayland_ei_false"
PACKAGECONFIG[xwayland_ei_false] = "-Dxwayland_ei=false"
PACKAGECONFIG[xwayland_ei_portal] = "-Dxwayland_ei=portal,,libei"
PACKAGECONFIG[xwayland_ei_socket] = "-Dxwayland_ei=socket,,libei"

do_install:append() {
    # remove files not needed and clashing with xserver-xorg
    rm -rf ${D}/${libdir}/xorg/
}

FILES:${PN} += "${libdir}/xorg/protocol.txt"

RDEPENDS:${PN} += "xkbcomp"
