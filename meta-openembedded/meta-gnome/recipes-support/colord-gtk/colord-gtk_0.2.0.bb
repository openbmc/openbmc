SUMMARY = "GTK support library for colord"
HOMEPAGE = "https://www.freedesktop.org/software/colord/"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

inherit meson gobject-introspection gettext gtk-doc features_check

DEPENDS = " \
    gtk+3 \
    colord \
"

SRC_URI = "http://www.freedesktop.org/software/colord/releases/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "66d048803c8b89e5e63da4b461484933"
SRC_URI[sha256sum] = "2a4cfae08bc69f000f40374934cd26f4ae86d286ce7de89f1622abc59644c717"

EXTRA_OEMESON = "-Dman=false"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

# colord
REQUIRED_DISTRO_FEATURES += "polkit"

UNKNOWN_CONFIGURE_WHITELIST_append = " introspection"
