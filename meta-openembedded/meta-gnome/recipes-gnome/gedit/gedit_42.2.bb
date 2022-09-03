SUMMARY = "GNOME editor"
SECTION = "x11/gnome"
LICENSE = "GPL-2.0-or-later"
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

SRC_URI[archive.sha256sum] = "3c6229111f0ac066ae44964920791d1265f5bbb56b0bd949a69b7b1261fc8fca"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

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
