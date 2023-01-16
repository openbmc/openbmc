SUMMARY = "A simple text editor"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    desktop-file-utils-native \
    libadwaita \
    gtk4 \
    gtksourceview5 \
    editorconfig-core-c \
    enchant2 \
"

GTKIC_VERSION = "4"

inherit gnomebase gtk-icon-cache itstool gnome-help mime-xdg features_check

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[archive.sha256sum] = "973a4b0deb68f9f91a54a4dd415b6afde9b5ae3ee684bc761471f94290f9f6cb"

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"
