SUMMARY = "xfce4-session is a session manager for Xfce 4 Desktop Environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"

DEPENDS = "libwnck3 libsm libxfce4ui virtual/libx11"

inherit xfce update-alternatives distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)}"
PACKAGECONFIG[polkit] = "--enable-polkit, --disable-polkit, polkit"

SRC_URI += " \
    file://0001-configure.in-hard-code-path-to-iceauth.patch \
"
SRC_URI[md5sum] = "635361f99a01b2d26c430a520b6d1314"
SRC_URI[sha256sum] = "14fb6db74eb69eb400673856ba307a0e57f577cf77c3561a5910151e3d52043f"

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

RDEPENDS_${PN} = " \
    dbus-x11 \
    iceauth \
    netbase \
    upower \
    xinit \
    xrdb \
"
