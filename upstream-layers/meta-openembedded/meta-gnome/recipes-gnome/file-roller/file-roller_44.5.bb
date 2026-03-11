SUMMARY = "An archive manager utility for the  GNOME Environment"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"


DEPENDS = " \
    desktop-file-utils-native \
    glib-2.0-native \
    glib-2.0 \
    json-glib \
    gtk4 \
    libadwaita \
    libarchive \
    libhandy \
    libportal \
"

inherit gnomebase gsettings itstool gobject-introspection gnome-help gettext upstream-version-is-even mime-xdg gtk-icon-cache features_check

SRC_URI[archive.sha256sum] = "dfaf4bb989c0b8986be8bdae9fffeab8d0f30669ae3a627e8c3df94f23888339"

REQUIRED_DISTRO_FEATURES = "opengl"

EXTRA_OEMESON += "-Dintrospection=enabled"

PACKAGECONFIG ??= ""

PACKAGECONFIG[nautilus] = "-Dnautilus-actions=enabled,-Dnautilus-actions=disabled,nautilus"
PACKAGECONFIG[packagekit] = "-Dpackagekit=true,-Dpackagekit=false,,packagekit"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${libdir}/nautilus \
"

EXTRA_OEMESON += "--cross-file=${WORKDIR}/meson-${PN}.cross"

do_write_config:append() {
    cat >${WORKDIR}/meson-${PN}.cross <<EOF
[binaries]
cpio = '${bindir}/cpio'
EOF
}
