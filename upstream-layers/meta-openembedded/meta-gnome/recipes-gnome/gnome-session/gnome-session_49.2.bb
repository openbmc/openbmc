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

SRC_URI[archive.sha256sum] = "fcdb4f45d6a60d84e9ecae1e3740bab6e55bab0cb49e0fb38280e9b38f3a8485"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"

PACKAGECONFIG[docbook] = "-Ddocbook=true, -Ddocbook=false"
PACKAGECONFIG[x11] = "-Dx11=true, -Dx11=false,virtual/libx11"
PACKAGECONFIG[manpages] = "-Dman=true, -Dman=false,xmlto-native libxslt-native"

FILES:${PN} += " \
   ${datadir}/xdg-desktop-portal \
    ${datadir}/xsessions \
    ${datadir}/wayland-sessions \
    ${systemd_user_unitdir} \
"
