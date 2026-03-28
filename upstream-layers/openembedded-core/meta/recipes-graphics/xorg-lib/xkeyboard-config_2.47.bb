SUMMARY = "Keyboard configuration database for X Window"

DESCRIPTION = "The non-arch keyboard configuration database for X \
Window.  The goal is to provide the consistent, well-structured, \
frequently released open source of X keyboard configuration data for X \
Window System implementations.  The project is targeted to XKB-based \
systems."

HOMEPAGE = "http://freedesktop.org/wiki/Software/XKeyboardConfig"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xkeyboard-config"

LICENSE = "MIT & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=faa756e04053029ddc602caf99e5ef1d"

SRC_URI = "${XORG_MIRROR}/individual/data/xkeyboard-config/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "e59984416a72d58b46a52bfec1b1361aa7d84354628227ee2783626c7a6db6b6"

SECTION = "x11/libs"
DEPENDS = "util-macros libxslt-native"

FILES:${PN} += "${datadir}/X11/xkb ${datadir}/xkeyboard-config-2"

inherit meson pkgconfig gettext python3native relative_symlinks

EXTRA_OEMESON += "-Dxorg-rules-symlinks=true"

BBCLASSEXTEND += "native"
