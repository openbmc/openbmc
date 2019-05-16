SUMMARY = "Portable C library for multiline text editing"
HOMEPAGE = "http://projects.gnome.org/gtksourceview/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = "gtk+3 libxml2 intltool-native gnome-common-native glib-2.0-native"

PNAME = "gtksourceview"

S = "${WORKDIR}/${PNAME}-${PV}"

inherit gnomebase lib_package gettext distro_features_check gtk-doc gobject-introspection

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtksourceview/4.2/${PNAME}-${PV}.tar.xz"
SRC_URI[md5sum] = "c9e6913c2fd30ca2fcdd71482faf8b99"
SRC_URI[sha256sum] = "c431eb234dc83c7819e58f77dd2af973252c7750da1c9d125ddc94268f94f675"

FILES_${PN} += "${datadir}/gtksourceview-4"
