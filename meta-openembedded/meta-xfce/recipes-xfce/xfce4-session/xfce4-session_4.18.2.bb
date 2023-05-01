SUMMARY = "xfce4-session is a session manager for Xfce 4 Desktop Environment"
SECTION = "x11"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libwnck3 libsm libxfce4ui virtual/libx11"

inherit xfce update-alternatives features_check

SRC_URI += "file://0001-configure.in-hard-code-path-to-iceauth.patch"
SRC_URI[sha256sum] = "01ff7fe8d4c2091adefc2925c6b7bb93152ba0732e95c9b9e64d8207fdbdeb8a"

REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)}"
PACKAGECONFIG[polkit] = "--enable-polkit, --disable-polkit, polkit"

ALTERNATIVE:${PN} = "x-session-manager"
ALTERNATIVE_TARGET[x-session-manager] = "${bindir}/xfce4-session"
ALTERNATIVE_PRIORITY_${PN} = "150"

FILES:${PN} += " \
    ${libdir}/xfce4/*/*/*.so \
    ${libdir}/xfce4/session/*-*-* \
    ${datadir}/xsessions \
    ${datadir}/themes/Default/balou/* \
    ${datadir}/polkit-1 \
"

RDEPENDS:${PN} = " \
    dbus-x11 \
    iceauth \
    netbase \
    upower \
    xinit \
    xrdb \
"
