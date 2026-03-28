SUMMARY = "xfce4-session is a session manager for Xfce 4 Desktop Environment"
HOMEPAGE = "https://docs.xfce.org/xfce/xfce4-session/start"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libwnck3 libsm libxfce4ui libxfce4windowing virtual/libx11"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"

inherit xfce update-alternatives features_check

SRC_URI += "file://0001-meson.build-hard-code-path-to-iceauth.patch"
SRC_URI[sha256sum] = "a8fe873fdb20366a44f1345400bfb29c2ff0cfe89dfefd852e2575464b80567c"

REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)}"
PACKAGECONFIG[polkit] = "-Dpolkit=enabled,-Dpolkit=disabled,polkit"

ALTERNATIVE:${PN} = "x-session-manager"
ALTERNATIVE_TARGET[x-session-manager] = "${bindir}/xfce4-session"
ALTERNATIVE_PRIORITY_${PN} = "150"

FILES:${PN} += " \
    ${libdir}/xfce4/*/*/*.so \
    ${libdir}/xfce4/session/*-*-* \
    ${datadir}/wayland-sessions \
    ${datadir}/xsessions \
    ${datadir}/themes/Default/balou/* \
    ${datadir}/polkit-1 \
    ${datadir}/xdg-desktop-portal/xfce-portals.conf \
    ${datadir}/xfce4 \
    ${datadir}/xfce4/labwc \
    ${datadir}/xfce4/labwc/labwc-environment \
    ${datadir}/xfce4/labwc/labwc-rc.xml \
"

RDEPENDS:${PN} = " \
    ${VIRTUAL-RUNTIME_dbus} \
    iceauth \
    netbase \
    upower \
    xinit \
    xrdb \
"
