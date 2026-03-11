SUMMARY = "GNOME session"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    glib-2.0-native \
    xtrans \
    libice \
    libsm \
    virtual/libx11 \
    gtk+3 \
    gnome-desktop \
    json-glib \
    systemd \
"


inherit gnomebase gettext gsettings upstream-version-is-even features_check

REQUIRED_DISTRO_FEATURES = "polkit systemd pam gobject-introspection-data"

SRC_URI = "https://download.gnome.org/sources/gnome-session/${@oe.utils.trim_version('${PV}', 1)}/gnome-session-${PV}.tar.xz"
SRC_URI[sha256sum] = "56ae9c68e49995793eb2096bcdc4533b111669e1e54c8b6e0b1d952f6a5e8a70"

PACKAGECONFIG ??= ""

PACKAGECONFIG[docbook] = "-Ddocbook=true, -Ddocbook=false"
PACKAGECONFIG[man] = "-Dman=true, -Dman=false,xmlto-native libxslt-native"

FILES:${PN} += " \
   ${datadir}/xdg-desktop-portal \
    ${datadir}/xsessions \
    ${datadir}/wayland-sessions \
    ${systemd_user_unitdir} \
"
