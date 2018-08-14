require gst-plugins.inc

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://common/coverage/coverage-report.pl;beginline=2;endline=17;md5=622921ffad8cb18ab906c56052788a3f \
                    file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605 \
                    file://gst/ffmpegcolorspace/utils.c;beginline=1;endline=20;md5=9c83a200b8e597b26ca29df20fc6ecd0"

DEPENDS += "alsa-lib libogg libvorbis libtheora util-linux tremor glib-2.0-native"

SRC_URI += "file://gst-plugins-base-tremor.patch \
            file://configure.ac-fix-subparse-plugin.patch \
            file://audioresample-Fix-build-on-x86-if-emmintrin.h-is-ava.patch \
            file://0001-aclocal.m4-don-t-do-crazy-things-to-GLIB_CFLAGS.patch \
"

SRC_URI[md5sum] = "776c73883e567f67b9c4a2847d8d041a"
SRC_URI[sha256sum] = "2cd3b0fa8e9b595db8f514ef7c2bdbcd639a0d63d154c00f8c9b609321f49976"

PR = "r8"

inherit gettext

EXTRA_OECONF += "--disable-freetypetest"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"

PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc"
PACKAGECONFIG[pango] = "--enable-pango,--disable-pango,pango"
PACKAGECONFIG[x11] = "--enable-x --enable-xvideo,--disable-x --disable-xvideo,virtual/libx11 libxv libsm libice"
PACKAGECONFIG[cdparanoia] = "--enable-cdparanoia,--disable-cdparanoia,cdparanoia"

do_configure_prepend() {
    # This m4 file contains nastiness which conflicts with libtool 2.2.2
    rm -f ${S}/m4/lib-link.m4
}

FILES_${PN} += "${datadir}/${BPN}"

CACHED_CONFIGUREVARS_append_x86 = " ac_cv_header_emmintrin_h=no ac_cv_header_xmmintrin_h=no"

# /usr/bin/gst-visualise-0.10 is a perl script.
RDEPENDS_${PN}-apps += "perl"
