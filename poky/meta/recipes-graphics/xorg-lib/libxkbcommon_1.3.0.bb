SUMMARY = "Generic XKB keymap library"
DESCRIPTION = "libxkbcommon is a keymap compiler and support library which \
processes a reduced subset of keymaps as defined by the XKB specification."
HOMEPAGE = "http://www.xkbcommon.org"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e525ed9809e1f8a07cf4bce8b09e8b87"
LICENSE = "MIT & MIT-style"

DEPENDS = "util-macros flex-native bison-native"

SRC_URI = "http://xkbcommon.org/download/${BPN}-${PV}.tar.xz"

SRC_URI[sha256sum] = "7b09e098ea69bc3054f0c57a9a25fda571c4df22398811606e32b5fffeb75e7b"

UPSTREAM_CHECK_URI = "http://xkbcommon.org/"

inherit meson pkgconfig

EXTRA_OEMESON = "-Denable-docs=false -Denable-xkbregistry=false"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'x11 wayland', d)}"
PACKAGECONFIG[x11] = "-Denable-x11=true,-Denable-x11=false,libxcb xkeyboard-config,"
PACKAGECONFIG[wayland] = "-Denable-wayland=true,-Denable-wayland=false,wayland-native wayland wayland-protocols,"

# Fix a following runtime error:
# xkbcommon: ERROR: couldn't find a Compose file for locale "C"
RDEPENDS_${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libx11-locale', 'libx11-compose-data', d)}"
