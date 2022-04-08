SUMMARY = "GTK support library for colord"
HOMEPAGE = "https://www.freedesktop.org/software/colord/"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

inherit meson gobject-introspection gettext gtk-doc features_check

DEPENDS = " \
    colord \
"

SRC_URI = "http://www.freedesktop.org/software/colord/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "b9466656d66d9a6ffbc2dd04fa91c8f6af516bf9efaacb69744eec0f56f3c1d0"

PACKAGECONFIG ??= "gtk3 gtk4"
PACKAGECONFIG[gtk3] = "-Dgtk3=true, -Dgtk3=false, gtk+3"
PACKAGECONFIG[gtk4] = "-Dgtk4=true, -Dgtk4=false, gtk4"

EXTRA_OEMESON = "-Dman=false"
GIR_MESON_OPTION = ""
# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

# colord
REQUIRED_DISTRO_FEATURES += "polkit"
