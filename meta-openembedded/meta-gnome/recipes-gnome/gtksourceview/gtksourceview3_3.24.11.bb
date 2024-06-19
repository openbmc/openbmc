SUMMARY = "Portable C library for multiline text editing"
HOMEPAGE = "http://projects.gnome.org/gtksourceview/"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

DEPENDS = "gtk+3 libxml2 glib-2.0-native"

PNAME = "gtksourceview"

S = "${WORKDIR}/${PNAME}-${PV}"

GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase lib_package gettext features_check gtk-doc gobject-introspection upstream-version-is-even

REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF += "--disable-glade-catalog --disable-gtk-doc --disable-Werror"

CFLAGS += "-Wno-error=incompatible-pointer-types"
SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtksourceview/3.24/${PNAME}-${PV}.tar.xz"
SRC_URI[md5sum] = "b748da426a7d64e1304f0c532b0f2a67"
SRC_URI[sha256sum] = "691b074a37b2a307f7f48edc5b8c7afa7301709be56378ccf9cc9735909077fd"

FILES:${PN} += " ${datadir}/gtksourceview-3.0"
