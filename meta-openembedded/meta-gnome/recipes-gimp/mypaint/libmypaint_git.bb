SUMMARY = "libmypaint is a library for making brushstrokes"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=9d13203ab4013e5a14dd62105f75d58b"

DEPENDS = " \
    glib-2.0-native \
    intltool-native \
    glib-2.0 \
    babl \
    json-c \
"

inherit autotools gobject-introspection gettext pkgconfig python3native

SRC_URI = "git://github.com/mypaint/libmypaint.git;protocol=https;branch=libmypaint-v1 \
           file://0001-make-build-compatible-w.-autoconf-2.7.patch \
          "
SRCREV = "2768251dacce3939136c839aeca413f4aa4241d0"
PV = "1.6.1"
S = "${WORKDIR}/git"

do_configure:append() {
    # autogen uses python2 so generate headers ourselves
    cd ${S}
    python3 generate.py mypaint-brush-settings-gen.h brushsettings-gen.h
}
