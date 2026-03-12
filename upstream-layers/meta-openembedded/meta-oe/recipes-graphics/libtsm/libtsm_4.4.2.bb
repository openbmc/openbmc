SUMMARY = "Terminal Emulator State Machine"
DESCRIPTION = "\
    TSM is a state machine for DEC VT100-VT520 compatible terminal emulators. \
    It tries to support all common standards while keeping compatibility to \
    existing emulators like xterm, gnome-terminal, konsole, etc. \
    TSM itself does not provide any rendering nor window management. It is a \
    simple plain state machine without any external dependencies. It can be \
    used to implement terminal emulators, but also to implement other \
    applications that need to interpret terminal escape sequences. \
"
HOMEPAGE = "https://github.com/kmscon/libtsm"
BUGTRACKER = "https://github.com/kmscon/libtsm/issues"
CVE_PRODUCT = "libtsm"
SECTION = "libs"

LICENSE = "MIT & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=69e8256cdc4e949f86fedf94b1b320b4 \
    file://LICENSE_htable;md5=2d5025d4aa3495befef8f17206a5b0a1 \
"

DEPENDS = "xkeyboard-config"

SRC_URI = "git://github.com/kmscon/libtsm;protocol=https;branch=main;tag=v${PV}"
SRCREV = "b052e48cc776be1cdb940be2dcc1603457c01c96"

inherit meson pkgconfig

EXTRA_OEMESON:append = " \
    -Dextra_debug=false \
    -Dtests=false \
    -Dgtktsm=false \
"
