SUMMARY = "xfce4-session is a session manager for Xfce 4 Desktop Environment"
HOMEPAGE = "https://docs.xfce.org/xfce/xfce4-session/start"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libwnck3 libsm libxfce4ui libxfce4windowing virtual/libx11"

inherit xfce update-alternatives features_check

SRC_URI += "file://0001-configure.in-hard-code-path-to-iceauth.patch"
SRC_URI[sha256sum] = "dbf00672c5316a30b7001fe852e6a5ba9f889afeab8a247545a160d4302f1fa2"

REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)}"
PACKAGECONFIG[polkit] = "--enable-polkit, --disable-polkit, polkit"

EXTRA_OECONF = "GDBUS_CODEGEN=${STAGING_BINDIR_NATIVE}/gdbus-codegen \
                GLIB_COMPILE_RESOURCES=${STAGING_BINDIR_NATIVE}/glib-compile-resources"

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
