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

SRC_URI[archive.sha256sum] = "6289b6afa44d3e4c999ce5761ff8b2c10ba309f55f9425f2b13f98208c81f6fb"

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
