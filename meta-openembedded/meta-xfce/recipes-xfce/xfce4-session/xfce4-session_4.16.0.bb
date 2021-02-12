SUMMARY = "xfce4-session is a session manager for Xfce 4 Desktop Environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"

DEPENDS = "libwnck3 libsm libxfce4ui virtual/libx11"

inherit xfce update-alternatives features_check

REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)}"
PACKAGECONFIG[polkit] = "--enable-polkit, --disable-polkit, polkit"

SRC_URI += "file://0001-configure.in-hard-code-path-to-iceauth.patch"
SRC_URI[sha256sum] = "22f273f212481d71e0b5618c62710cd85f69aea74f5ea5c0093f7918b07d17b7"

ALTERNATIVE_${PN} = "x-session-manager"
ALTERNATIVE_TARGET[x-session-manager] = "${bindir}/xfce4-session"
ALTERNATIVE_PRIORITY_${PN} = "150"

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
