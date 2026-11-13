SUMMARY = "A simple text editor"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"


DEPENDS = " \
    desktop-file-utils-native \
    libadwaita \
    gtk4 \
    gtksourceview5 \
    editorconfig-core-c \
    libspelling \
"

GTKIC_VERSION = "4"

inherit gnomebase gtk-icon-cache itstool gnome-help mime-xdg features_check

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[archive.sha256sum] = "2b76e6da1506346c54b36ba1a3aeedcd28f4cb7d6a8793d217f209456c92dd4c"

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"
