SUMMARY = "GNOME session"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    glib-2.0 \
    gtk4 \
    gnome-desktop \
    json-glib \
    systemd \
"

inherit gnomebase gettext gsettings upstream-version-is-even mime mime-xdg manpages features_check

REQUIRED_DISTRO_FEATURES = "systemd"

SRC_URI[archive.sha256sum] = "5195c4f60542a953ab6ebd288f3785b8e7746292d8c167e0c99d353438a184d1"

UPSTREAM_CHECK_URI = "https://gitlab.gnome.org/GNOME/gnome-session/-/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

PACKAGECONFIG[docbook] = "-Ddocbook=true, -Ddocbook=false"
PACKAGECONFIG[manpages] = "-Dman=true, -Dman=false,xmlto-native libxslt-native"

FILES:${PN} += " \
   ${datadir}/xdg-desktop-portal \
    ${datadir}/xsessions \
    ${datadir}/wayland-sessions \
    ${systemd_user_unitdir} \
"
