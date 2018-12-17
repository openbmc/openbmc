SUMMARY = "Portable C library for multiline text editing"
HOMEPAGE = "http://projects.gnome.org/gtksourceview/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = "gtk+3 libxml2 intltool-native gnome-common-native glib-2.0-native"

PNAME = "gtksourceview"

S = "${WORKDIR}/${PNAME}-${PV}"

inherit gnomebase lib_package gettext distro_features_check gtk-doc gobject-introspection

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtksourceview/3.24/${PNAME}-${PV}.tar.xz"
SRC_URI[md5sum] = "52f8c83ad21ad75e9ee6cca03ce2e63f"
SRC_URI[sha256sum] = "1e9bb8ff190db705deb916dd23ff681f0e8803aec407bf0fd64c7e615ac436fe"

FILES_${PN} += " ${datadir}/gtksourceview-3.0"
