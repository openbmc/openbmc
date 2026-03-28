SUMMARY = "Tepl library eases the development of GtkSourceView-based projects"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-3.0-or-later.txt;md5=c51d3eef3be114124d11349ca0d7e117"

DEPENDS = " \
    gsettings-desktop-schemas \
    glib-2.0 \
    gtk+3 \
    libgedit-amtk \
    libgedit-gfls \
    libgedit-gtksourceview \
    libhandy \
    libxml2 \
    uchardet \
    gtk-doc-native \
    libxslt-native \
    docbook-xsl-stylesheets-native \
    python3-pygments-native \
"

inherit meson gobject-introspection gettext features_check pkgconfig


ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
SRC_URI = "git://gitlab.gnome.org/World/gedit/libgedit-tepl.git;protocol=https;branch=main;tag=${PV}"
SRCREV = "d60f7ded17b52ea42091c073ea81090e91f38620"
# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES += "gobject-introspection-data"
GIR_MESON_OPTION = ""

GTKDOC_MESON_OPTION = "gtk_doc"
