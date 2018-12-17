SUMMARY = "GNOME keyboard library"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=6e29c688d912da12b66b73e32b03d812"

SECTION = "x11/gnome/libs"

DEPENDS = "gconf glib-2.0 libxklavier gtk+3 intltool-native"

inherit distro_features_check gnome gobject-introspection gettext

REQUIRED_DISTRO_FEATURES = "x11"

GNOME_COMPRESS_TYPE = "xz"

SRC_URI[archive.md5sum] = "8b9d13d46255cde910b3db5a6ebb4727"
SRC_URI[archive.sha256sum] = "ea3b418c57c30615f7ee5b6f718def7c9d09ce34637324361150744258968875"

EXTRA_OECONF_remove = "--disable-schemas-install"

