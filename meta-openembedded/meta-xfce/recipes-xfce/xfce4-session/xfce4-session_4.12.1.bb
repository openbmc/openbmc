SUMMARY = "xfce4-session is a session manager for Xfce 4 Desktop Environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"
DEPENDS = "virtual/libx11 libsm libxfce4util libxfce4ui gtk+ libwnck dbus dbus-glib xfconf polkit"
RDEPENDS_${PN} = "netbase xinit dbus-x11 iceauth upower"

inherit xfce update-alternatives distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "f4921fb2e606e74643daf1212263076c"
SRC_URI[sha256sum] = "97d7f2a2d0af7f3623b68d1f04091e02913b28f9555dab8b0d26c8a1299d08fd"
SRC_URI += " \
    file://0001-configure.in-hard-code-path-to-iceauth.patch \
"

ALTERNATIVE_${PN} = "x-session-manager"
ALTERNATIVE_TARGET[x-session-manager] = "${bindir}/xfce4-session"
ALTERNATIVE_PRIORITY_${PN} = "100"

FILES_${PN} += " \
    ${libdir}/xfce4/*/*/*.so \
    ${libdir}/xfce4/session/*-*-* \
    ${datadir}/xsessions \
    ${datadir}/themes/Default/balou/* \
    ${datadir}/polkit-1 \
"

FILES_${PN}-dbg += "${libdir}/xfce4/*/*/.debug"

RDEPENDS_${PN} += "machine-host"
