SUMMARY = "Intelligent Input Bus for Linux/Unix"
HOMEPAGE = "https://github.com/ibus/ibus/wiki"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

inherit autotools-brokensep pkgconfig gobject-introspection gettext gtk-doc vala

PV = "1.5.22"

DEPENDS = "unicode-ucd"

SRC_URI = " \
    git://github.com/ibus/ibus.git \
    file://0001-Do-not-try-to-start-dbus-we-do-not-have-dbus-lauch.patch \
"
SRCREV = "e3262f08b9e3efc57808700823b0622ec03a1b5f"
S = "${WORKDIR}/git"

PACKAGECONFIG[appindicator] = "--enable-appindicator,--disable-appindicator,qtbase"
PACKAGECONFIG[dconf] = "--enable-dconf,--disable-dconf,dconf"
PACKAGECONFIG[gtk2] = "--enable-gtk2,--disable-gtk2,gtk+"
PACKAGECONFIG[gtk3] = "--enable-gtk3,--disable-gtk3,gtk+3"
PACKAGECONFIG[wayland]  = "--enable-wayland,--disable-wayland,wayland"
PACKAGECONFIG[vala]  = "--enable-vala,--disable-vala"
PACKAGECONFIG[x11]  = "--enable-xim --enable-ui,--disable-xim --disable-ui,virtual/libx11"

EXTRA_OECONF = " \
    --disable-tests \
    --disable-emoji-dict \
    --disable-python2 \
"

do_configure_prepend() {
    touch ${S}/ChangeLog
    touch ${S}/ABOUT-NLS
}
