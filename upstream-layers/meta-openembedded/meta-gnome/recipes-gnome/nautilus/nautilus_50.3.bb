SUMMARY = "File manager for GNOME"
SECTION = "x11/gnome"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"


DEPENDS = " \
    appstream-glib-native \
    blueprint-compiler-native \
    desktop-file-utils-native \
    glib-2.0 \
    glycin \
    gnome-autoar \
    gnome-desktop \
    gtk4 \
    libadwaita \
    libportal \
    libxml2 \
    tinysparql \
    wayland \
    wayland-native \
"

inherit gnomebase gsettings gobject-introspection gi-docgen gettext features_check mime-xdg gtk-icon-cache

SRC_URI[archive.sha256sum] = "63773baf0ddec7b4875fc2c22040378562ea1e6812fe0a2e29c9c8d5c72807f8"

REQUIRED_DISTRO_FEATURES = "opengl gobject-introspection-data"
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

GIDOCGEN_MESON_OPTION = "docs"
GIDOCGEN_MESON_ENABLE_FLAG = 'true'
GIDOCGEN_MESON_DISABLE_FLAG = 'false'

EXTRA_OEMESON += " \
    -Dtests=none \
"

export GI_TYPELIB_PATH = "${STAGING_LIBDIR}/girepository-1.0/"

PACKAGECONFIG = "cloudproviders extensions"
PACKAGECONFIG[cloudproviders] = "-Dcloudproviders=enabled,-Dcloudproviders=disabled,libcloudproviders"
PACKAGECONFIG[extensions] = "-Dextensions=true,-Dextensions=false, gexiv2 gstreamer1.0-plugins-base gdk-pixbuf"
PACKAGECONFIG[selinux] = "-Dselinux=enabled,-Dselinux=disabled,libselinux"

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
