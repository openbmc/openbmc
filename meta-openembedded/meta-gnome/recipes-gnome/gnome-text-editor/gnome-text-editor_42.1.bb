SUMMARY = "A simple text editor"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    desktop-file-utils-native \
    libadwaita \
    gtk4 \
    gtksourceview5 \
    enchant2 \
"

GTKIC_VERSION = "4"

inherit gnomebase gtk-icon-cache itstool gnome-help mime-xdg features_check

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[archive.sha256sum] = "a3c8508033bfb63a8b48a062ac1e67b2c333ba0153879b38c661968a103ad15c"

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"
