SUMMARY = "GNOME session"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = " \
    glib-2.0-native \
    libxslt-native \
    xmlto-native \
    xtrans \
    libice \
    libsm \
    virtual/libx11 \
    gtk+3 \
    gnome-desktop \
    gsettings-desktop-schemas \
    json-glib \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gettext gsettings upstream-version-is-even features_check

REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam gobject-introspection-data"

SRC_URI[archive.sha256sum] = "ee4a229053f522624054889609335b885287cf67bbde0dc9fd882b01ec9b5b39"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', 'consolekit',d)}"

PACKAGECONFIG[consolekit] = "-Dconsolekit=true, -Dconsolekit=false, consolekit"
PACKAGECONFIG[systemd] = "-Dsystemd=true -Dsystemd_journal=true, -Dsystemd=false -Dsystemd_journal=false, systemd"

FILES:${PN} += " \
    ${datadir}/xsessions \
    ${datadir}/wayland-sessions \
    ${systemd_user_unitdir} \
"

RDEPENDS:${PN} += "gnome-shell gnome-settings-daemon gsettings-desktop-schemas"
