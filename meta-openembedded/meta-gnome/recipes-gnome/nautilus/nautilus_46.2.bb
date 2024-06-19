SUMMARY = "File manager for GNOME"
SECTION = "x11/gnome"

LICENSE="GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"


DEPENDS = " \
    appstream-glib-native \
    desktop-file-utils-native \
    glib-2.0 \
    gnome-autoar \
    gnome-desktop \
    gtk4 \
    libadwaita \
    libcloudproviders \
    libhandy \
    libportal \
    libxml2 \
    tracker \
"

inherit gnomebase gsettings gobject-introspection gi-docgen gettext features_check mime-xdg gtk-icon-cache

SRC_URI[archive.sha256sum] = "6ee8c99019b9e3447f6918d68232a20deca89e5525c05805432b7d8840ca71fa"

REQUIRED_DISTRO_FEATURES = "x11 opengl gobject-introspection-data"

GIDOCGEN_MESON_OPTION = "docs"
GIDOCGEN_MESON_ENABLE_FLAG = 'true'
GIDOCGEN_MESON_DISABLE_FLAG = 'false'

EXTRA_OEMESON += " \
    -Dtests=none \
"

PACKAGECONFIG = "extensions"
PACKAGECONFIG[extensions] = "-Dextensions=true,-Dextensions=false, gexiv2 gstreamer1.0-plugins-base gdk-pixbuf"
PACKAGECONFIG[packagekit] = "-Dpackagekit=true,-Dpackagekit=false,packagekit"

do_install:prepend() {
    sed -i -e 's|${B}/||g' ${B}/src/nautilus-enum-types.c
}

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/gnome-shell \
    ${datadir}/tracker3 \
"

# mandatory - not checked during configuration:
# | (org.gnome.Nautilus:863): GLib-GIO-ERROR **: 21:03:52.326: Settings schema 'org.freedesktop.Tracker.Miner.Files' is not installed
RDEPENDS:${PN} += "tracker-miners bubblewrap"
