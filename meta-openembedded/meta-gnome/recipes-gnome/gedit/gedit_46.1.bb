SUMMARY = "GNOME editor"
SECTION = "x11/gnome"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    appstream-glib-native \
    desktop-file-utils-native \
    libgedit-amtk \
    libgedit-gtksourceview \
    gdk-pixbuf-native \
    gtk+3 \
    gsettings-desktop-schemas \
    libpeas \
    libsoup \
    gspell \
    tepl \
"

inherit gnomebase gsettings itstool gnome-help gobject-introspection gtk-doc vala gettext features_check mime-xdg gtk-icon-cache python3targetconfig

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

SRC_URI[archive.sha256sum] = "a1a6e37f041765dff7227a1f5578b6f49faaf016b1e17e869caf5bfb94c6aa4e"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

do_install:prepend() {
    sed -i -e 's|${B}||g' ${B}/plugins/filebrowser/gedit-file-browser-enum-types.c
}

GIR_MESON_OPTION = ""

GTKDOC_MESON_OPTION = "gtk_doc"

PACKAGES += "${PN}-python"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"

FILES:${PN}-python += " \
    ${PYTHON_SITEPACKAGES_DIR} \
"

RDEPENDS:${PN} += "gsettings-desktop-schemas"
RRECOMMENDS:${PN} += "source-code-pro-fonts"
