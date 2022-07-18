SUMMARY = "Keyboard configuration database for X Window"

DESCRIPTION = "The non-arch keyboard configuration database for X \
Window.  The goal is to provide the consistent, well-structured, \
frequently released open source of X keyboard configuration data for X \
Window System implementations.  The project is targeted to XKB-based \
systems."

HOMEPAGE = "http://freedesktop.org/wiki/Software/XKeyboardConfig"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xkeyboard-config"

LICENSE = "MIT & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0e7f21ca7db975c63467d2e7624a12f9"

SRC_URI = "${XORG_MIRROR}/individual/data/xkeyboard-config/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "1f1bb1292a161d520a3485d378609277d108cd07cde0327c16811ff54c3e1595"

SECTION = "x11/libs"
DEPENDS = "util-macros libxslt-native"

EXTRA_OECONF = "--with-xkb-rules-symlink=xorg --disable-runtime-deps"

FILES:${PN} += "${datadir}/X11/xkb"

inherit meson pkgconfig gettext python3native

do_install:append () {
    install -d ${D}${datadir}/X11/xkb/compiled
    cd ${D}${datadir}/X11/xkb/rules && ln -sf base xorg
}
