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
SRC_URI[sha256sum] = "d53afac6c71953f5cf66d03d289dacd8961da5bd309c1dff125d5955d9db5f76"

UPSTREAM_CHECK_REGEX = "xwayland-(?P<pver>\d+(\.(?!90\d)\d+)+)\.tar"

inherit meson features_check pkgconfig
REQUIRED_DISTRO_FEATURES = "x11 opengl"

DEPENDS += "xorgproto xtrans pixman libxkbfile libxfont2 wayland wayland-native wayland-protocols libdrm libepoxy libxcvt"

OPENGL_PKGCONFIGS = "glx glamor dri3"
PACKAGECONFIG ??= "${XORG_CRYPTO} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', '${OPENGL_PKGCONFIGS}', '', d)} \
"
PACKAGECONFIG[dri3] = "-Ddri3=true,-Ddri3=false,libxshmfence"
PACKAGECONFIG[glx] = "-Dglx=true,-Dglx=false,virtual/libgl virtual/libx11"
PACKAGECONFIG[glamor] = "-Dglamor=true,-Dglamor=false,libepoxy virtual/libgbm,libegl"
PACKAGECONFIG[unwind] = "-Dlibunwind=true,-Dlibunwind=false,libunwind"
PACKAGECONFIG[xinerama] = "-Dxinerama=true,-Dxinerama=false"

# Xorg requires a SHA1 implementation, pick one
XORG_CRYPTO ??= "openssl"
PACKAGECONFIG[openssl] = "-Dsha1=libcrypto,,openssl"
PACKAGECONFIG[nettle] = "-Dsha1=libnettle,,nettle"
PACKAGECONFIG[gcrypt] = "-Dsha1=libgcrypt,,libgcrypt"

do_install:append() {
    # remove files not needed and clashing with xserver-xorg
    rm -rf ${D}/${libdir}/xorg/
}

FILES:${PN} += "${libdir}/xorg/protocol.txt"

RDEPENDS:${PN} += "xkbcomp"
