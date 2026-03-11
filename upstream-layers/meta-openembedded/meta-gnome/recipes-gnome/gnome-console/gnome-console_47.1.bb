SUMMARY = "GNOME Console"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

GTKIC_VERSION = "4"
inherit gnomebase gsettings pkgconfig gtk-icon-cache
REQUIRED_DISTRO_FEATURES = "opengl"

DEPENDS = " \
    desktop-file-utils-native \
    gtk4-native \
    glib-2.0 \
    gsettings-desktop-schemas \
    hicolor-icon-theme \
    libadwaita \
    libgtop \
    pcre2 \
    vte \
"
SRC_URI += "file://0001-include-locale.h-for-setlocale.patch"
SRC_URI[archive.sha256sum] = "d3f600b45b51716691aee92870332e9c9a8ca89d553565f3131d9e28074725d0"

PACKAGECONFIG ?= ""
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false"
PACKAGECONFIG[devel] = "-Ddevel=true,-Ddevel=false"

FILES:${PN} += "${datadir}"
