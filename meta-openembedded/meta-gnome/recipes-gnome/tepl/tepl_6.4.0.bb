SUMMARY = "Tepl library eases the development of GtkSourceView-based projects"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-3.0-or-later.txt;md5=c51d3eef3be114124d11349ca0d7e117"

DEPENDS = " \
    gsettings-desktop-schemas \
    glib-2.0 \
    gtk+3 \
    gtksourceview4 \
    amtk \
    libxml2 \
    uchardet \
    gtk-doc-native \
    libxslt-native \
    docbook-xsl-stylesheets-native \
    python3-pygments-native \
"

inherit meson gobject-introspection gettext features_check pkgconfig


ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
SRC_URI = "git://gitlab.gnome.org/swilmet/tepl;protocol=https;branch=main"
SRCREV = "d1e9cc2e4a0738f68d9002f13724b7239f075844"
S = "${WORKDIR}/git"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES += "gobject-introspection-data"
GIR_MESON_OPTION = ""

GTKDOC_MESON_OPTION = "gtk_doc"
