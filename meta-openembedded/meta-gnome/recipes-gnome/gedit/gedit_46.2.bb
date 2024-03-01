SUMMARY = "GNOME editor"
SECTION = "x11/gnome"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"


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

inherit gnomebase gsettings itstool gnome-help gobject-introspection gtk-doc gettext features_check mime-xdg gtk-icon-cache python3targetconfig

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

SRC_URI += "file://0001-fix-for-clang-18.patch"
SRC_URI[archive.sha256sum] = "c0866412bad147ebace2d282ffcbb5a0e9a304b20fd55640bee21c81e6d501ef"

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
