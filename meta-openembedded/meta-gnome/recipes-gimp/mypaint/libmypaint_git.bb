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

inherit autotools gobject-introspection gettext python3native

SRC_URI = "git://github.com/mypaint/libmypaint.git;protocol=https;branch=libmypaint-v1"
SRCREV = "477cb94b596035b54a255faaf95d13f6a8ee3619"
PV = "1.4.0"
S = "${WORKDIR}/git"

do_configure_append() {
    # autogen uses python2 so generate headers ourselves
    cd ${S}
    python3 generate.py mypaint-brush-settings-gen.h brushsettings-gen.h
}
