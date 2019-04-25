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
SRC_URI[md5sum] = "e43337d17c40eae440a76630db76c54e"
SRC_URI[sha256sum] = "ab5406cdd0bdcf66afcd52b1e048a2f43c0f3ab391644ee30d9ac419b93cd59b"

FILES_${PN} += " ${datadir}/gtksourceview-3.0"
