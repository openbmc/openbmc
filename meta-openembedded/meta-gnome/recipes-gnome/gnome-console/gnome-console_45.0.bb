SUMMARY = "GNOME Console"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=8f0e2cd40e05189ec81232da84bd6e1a"

GTKIC_VERSION = "4"
inherit gnomebase gsettings pkgconfig gtk-icon-cache

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

SRC_URI[archive.sha256sum] = "e7462128d2df2324a1d748062c40429cd0504af09e407067b33f3a9d0c59c8e1"

PACKAGECONFIG ?= ""
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false"
PACKAGECONFIG[devel] = "-Ddevel=true,-Ddevel=false"

FILES:${PN} += "${datadir}"
