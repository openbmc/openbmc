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

SRC_URI[archive.sha256sum] = "e4950207f0547e6a6c0f18eebfcf6e1a10461eab0f2fae0aae512b1044c7ac6e"

PACKAGECONFIG ?= ""
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false"
PACKAGECONFIG[devel] = "-Ddevel=true,-Ddevel=false"

FILES:${PN} += "${datadir}"
