SUMMARY = "Tepl library eases the development of GtkSourceView-based projects"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-3.0-or-later.txt;md5=c51d3eef3be114124d11349ca0d7e117"

DEPENDS = " \
    glib-2.0-native \
    gtk+3 \
    gtksourceview4 \
    amtk \
    libxml2 \
    uchardet \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gobject-introspection gettext features_check

# for gtksourceview4
REQUIRED_DISTRO_FEATURES += "x11"

SRC_URI[archive.sha256sum] = "a86397a895dca9c0de7a5ccb063bda8f7ef691cccb950ce2cfdee367903e7a63"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES += "gobject-introspection-data"
GIR_MESON_OPTION = ""

GTKDOC_MESON_OPTION = "gtk_doc"
