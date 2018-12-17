SUMMARY = "xfce4-session is a session manager for Xfce 4 Desktop Environment"
SECTION = "x11"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"
DEPENDS = "virtual/libx11 libsm libxfce4util libxfce4ui gtk+ libwnck3 dbus dbus-glib xfconf polkit"
RDEPENDS_${PN} = "netbase xinit dbus-x11 iceauth upower"

inherit xfce update-alternatives distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " \
    file://0001-configure.in-hard-code-path-to-iceauth.patch \
"
SRC_URI[md5sum] = "a47988a2c871a411c19e3af1eefe591e"
SRC_URI[sha256sum] = "c789f0f8234e06f5266f0c6ccdbdcc3c085e8d9eea06a0eafe8f7cfc4fe23af4"

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

FILES_${PN} += " \
    ${libdir}/xfce4/session/splash-engines/*.la \
"

RDEPENDS_${PN} += "machine-host"
