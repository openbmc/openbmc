SUMMARY = "xfce4-session is a session manager for Xfce 4 Desktop Environment"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libwnck3 libsm libxfce4ui libxfce4windowing virtual/libx11"

inherit xfce update-alternatives features_check

SRC_URI += "file://0001-configure.in-hard-code-path-to-iceauth.patch"
SRC_URI[sha256sum] = "5229233fe6ee692361cc28724886c5b08e0216d89f09c42d273191d38fd64f85"

REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)}"
PACKAGECONFIG[polkit] = "--enable-polkit, --disable-polkit, polkit"

EXTRA_OECONF = "GDBUS_CODEGEN=${STAGING_BINDIR_NATIVE}/gdbus-codegen"

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
"

RDEPENDS:${PN} = " \
    ${VIRTUAL-RUNTIME_dbus} \
    iceauth \
    netbase \
    upower \
    xinit \
    xrdb \
"
