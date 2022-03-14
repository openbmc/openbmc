SUMMARY = "GNOME editor"
SECTION = "x11/gnome"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    gdk-pixbuf-native \
    gtk+3 \
    gsettings-desktop-schemas \
    libpeas \
    libsoup-2.4 \
    gspell \
    gtksourceview4 \
    tepl \
"

inherit gnomebase gsettings itstool gnome-help gobject-introspection gtk-doc vala gettext features_check mime-xdg python3targetconfig

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " file://0001-Remove-incorrect-args-for-i18n.merge_file.patch"
SRC_URI[archive.sha256sum] = "55e394a82cb65678b1ab49526cf5bd43f00d8fba21476a4849051a8e137d3691"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
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
