SUMMARY = "Intelligent Input Bus for Linux/Unix"
HOMEPAGE = "https://github.com/ibus/ibus/wiki"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

inherit autotools-brokensep pkgconfig gobject-introspection gettext gtk-doc vala

PV = "1.5.26"

DEPENDS = "unicode-ucd"

SRC_URI = " \
    git://github.com/ibus/ibus.git;branch=main;protocol=https \
    file://0001-Do-not-try-to-start-dbus-we-do-not-have-dbus-lauch.patch \
    file://0001-makefile-don-t-gzip-the-man-pages.patch \
"
SRCREV = "6a70ab0338206bd1c7d01a4e1874ea0ee5b3a9d3"
S = "${WORKDIR}/git"

PACKAGECONFIG[appindicator] = "--enable-appindicator,--disable-appindicator,qtbase"
PACKAGECONFIG[dconf] = "--enable-dconf,--disable-dconf,dconf"
PACKAGECONFIG[gtk2] = "--enable-gtk2,--disable-gtk2,gtk+"
PACKAGECONFIG[gtk3] = "--enable-gtk3,--disable-gtk3,gtk+3"
PACKAGECONFIG[gtk4] = "--enable-gtk4,--disable-gtk4,gtk4"
PACKAGECONFIG[systemd] = "--enable-systemd-services,--disable-systemd-services,systemd"
PACKAGECONFIG[wayland]  = "--enable-wayland,--disable-wayland,wayland"
PACKAGECONFIG[vala]  = "--enable-vala,--disable-vala"
PACKAGECONFIG[x11]  = "--enable-xim --enable-ui,--disable-xim --disable-ui,virtual/libx11"

EXTRA_OECONF = " \
    --disable-tests \
    --disable-emoji-dict \
    --disable-python2 \
"
EXTRA_OECONF:append:class-target = "\
    --with-python=${bindir}/python3 \
"

do_configure:prepend() {
    touch ${S}/ChangeLog
    touch ${S}/ABOUT-NLS
}
