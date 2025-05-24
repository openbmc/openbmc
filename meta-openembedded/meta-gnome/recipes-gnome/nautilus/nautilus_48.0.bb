SUMMARY = "File manager for GNOME"
SECTION = "x11/gnome"

LICENSE = "GPL-3.0-only"
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
    tinysparql \
    wayland \
    wayland-native \
"

inherit gnomebase gsettings gobject-introspection gi-docgen gettext features_check mime-xdg gtk-icon-cache

SRC_URI[archive.sha256sum] = "f9e6a4cbe1d5bb7ad59ac44f0d439edd4b0507bf07d3d90a2e7e7e6d3f416d57"

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
    ${datadir}/localsearch3 \
"

# mandatory - not checked during configuration:
# | (org.gnome.Nautilus:863): GLib-GIO-ERROR **: 21:03:52.326: Settings schema 'org.freedesktop.Tracker.Miner.Files' is not installed
RDEPENDS:${PN} += "localsearch bubblewrap"
