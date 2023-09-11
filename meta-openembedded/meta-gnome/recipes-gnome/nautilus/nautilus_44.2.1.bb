SUMMARY = "File manager for GNOME"
SECTION = "x11/gnome"

LICENSE="GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"

GNOMEBASEBUILDCLASS = "meson"

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

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

SRC_URI += "file://0001-Replace-filename-with-basename.patch"
SRC_URI[archive.sha256sum] = "d9c62f024727f7a76fc6a5da788a2b0247df01a71c2a601143c62aac0ff41e4c"

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

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/gnome-shell \
    ${datadir}/tracker3 \
"

# mandatory - not checked during configuration:
# | (org.gnome.Nautilus:863): GLib-GIO-ERROR **: 21:03:52.326: Settings schema 'org.freedesktop.Tracker.Miner.Files' is not installed
RDEPENDS:${PN} += "tracker-miners bubblewrap"
