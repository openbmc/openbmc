SUMMARY = "Generic XKB keymap library"
DESCRIPTION = "libxkbcommon is a keymap compiler and support library which \
processes a reduced subset of keymaps as defined by the XKB specification."
HOMEPAGE = "http://www.xkbcommon.org"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e525ed9809e1f8a07cf4bce8b09e8b87"
LICENSE = "MIT & MIT-style"

DEPENDS = "util-macros flex-native bison-native"

SRC_URI = "http://xkbcommon.org/download/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "7d0e4c4a137d0ac45bf6b328c84c3a81"
SRC_URI[sha256sum] = "e829265db04e0aebfb0591b6dc3377b64599558167846c3f5ee5c5e53641fe6d"

UPSTREAM_CHECK_URI = "http://xkbcommon.org/"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-docs"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,libxcb xkeyboard-config,"
