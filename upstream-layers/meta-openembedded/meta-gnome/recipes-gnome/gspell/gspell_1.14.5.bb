SUMMARY = "gspell adds spell-checking to a GTK+ applications"
HOMEPAGE = "https://wiki.gnome.org/Projects/gspell"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-2.1-or-later.txt;md5=8c2e1ec1540fb3e0beb68361344cba7e"

DEPENDS = "gtk+3 icu enchant2"

inherit gnomebase gettext gobject-introspection vala gtk-doc

GIR_MESON_OPTION = "gobject_introspection"
GTKDOC_MESON_OPTION = "gtk_doc"

EXTRA_OEMESON = "-Dgspell_app=false -Dtests=false -Dinstall_tests=false"

SRC_URI[archive.sha256sum] = "788783b56fc3d03283b3aa29302d0b958aeca88835e10b1ec61f19e0efc7e8a4"
