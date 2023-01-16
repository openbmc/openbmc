SUMMARY = "Actions, Menus and Toolbars Kit"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-3.0-or-later.txt;md5=c51d3eef3be114124d11349ca0d7e117"

DEPENDS = " \
    glib-2.0-native \
    gtk+3 \
    gtk-doc-native \
    libxslt-native \
    docbook-xsl-stylesheets-native \
    python3-pygments-native \
"

GNOMEBASEBUILDCLASS = "meson"

GIR_MESON_OPTION = ""

inherit gnomebase gettext features_check gobject-introspection

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI[archive.sha256sum] = "d50115b85c872aac296934b5ee726a3fa156c6f5ad96d27e0edd0aa5ad173228"
