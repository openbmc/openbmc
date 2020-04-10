SUMMARY = "GNOME session"
LICENSE = "GPLv2"
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
    gnome-desktop3 \
    gsettings-desktop-schemas \
    json-glib \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gettext gsettings upstream-version-is-even features_check

REQUIRED_DISTRO_FEATURES = "x11 polkit systemd pam gobject-introspection-data"

SRC_URI[archive.md5sum] = "38dcdb844a0349cc2fb10998095543e4"
SRC_URI[archive.sha256sum] = "8bb77381650a03ae4be7260751363bd27da93f09bac1ddbc885f4ce44ebaf8e1"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', 'consolekit',d)}"

PACKAGECONFIG[consolekit] = "-Dconsolekit=true, -Dconsolekit=false, consolekit"
PACKAGECONFIG[systemd] = "-Dsystemd=true -Dsystemd_journal=true, -Dsystemd=false -Dsystemd_journal=false, systemd"

FILES_${PN} += " \
    ${datadir}/xsessions \
    ${datadir}/wayland-sessions \
    ${systemd_user_unitdir} \
"

RDEPENDS_${PN} += "gnome-shell gnome-settings-daemon gsettings-desktop-schemas"
