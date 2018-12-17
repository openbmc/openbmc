SUMMARY = "GStreamer multimedia framework"
DESCRIPTION = "GStreamer is a multimedia framework for encoding and decoding video and sound. \
It supports a wide range of formats including mp3, ogg, avi, mpeg and quicktime."
HOMEPAGE = "http://gstreamer.freedesktop.org/"
BUGTRACKER = "https://bugzilla.gnome.org/enter_bug.cgi?product=Gstreamer"
SECTION = "multimedia"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605 \
                    file://gst/gst.h;beginline=1;endline=21;md5=8e5fe5e87d33a04479fde862e238eaa4"
DEPENDS = "glib-2.0 libxml2 bison-native flex-native glib-2.0-native"

PR = "r2"

SRC_URI = "http://gstreamer.freedesktop.org/src/gstreamer/gstreamer-${PV}.tar.bz2 \
           file://check_fix.patch \
           file://gst-inspect-check-error.patch \
           file://0001-baseparse-Fix-self-comparison-always-evaluates-to-tr.patch \
           file://0001-parse-make-grammar.y-work-with-Bison-3.patch \
           file://0002-gst-glib2.m4-don-t-do-crazy-things-to-GLIB_CFLAGS.patch \
"

SRC_URI[md5sum] = "a0cf7d6877f694a1a2ad2b4d1ecb890b"
SRC_URI[sha256sum] = "e556a529e0a8cf1cd0afd0cab2af5488c9524e7c3f409de29b5d82bb41ae7a30"

inherit autotools pkgconfig gettext

GSTREAMER_DEBUG ?= "--disable-debug"
EXTRA_OECONF = "--disable-docbook --disable-gtk-doc \
            --disable-dependency-tracking --disable-check \
            --disable-examples --disable-tests \
            --disable-valgrind ${GSTREAMER_DEBUG} \
            --disable-introspection \
            "

CACHED_CONFIGUREVARS += "ac_cv_header_valgrind_valgrind_h=no"

# apply gstreamer hack after Makefile.in.in in source is replaced by our version from
# ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in, but before configure is executed
# http://lists.linuxtogo.org/pipermail/openembedded-core/2012-November/032233.html
oe_runconf_prepend() {
        sed -i -e "1a\\" -e 'GETTEXT_PACKAGE = @GETTEXT_PACKAGE@' ${S}/po/Makefile.in.in
}

#do_compile_prepend () {
#    mv ${WORKDIR}/gstregistrybinary.[ch] ${S}/gst/
#}

FILES_${PN} += " ${libdir}/gstreamer-0.10/*.so"
FILES_${PN}-dev += " ${libdir}/gstreamer-0.10/*.la ${libdir}/gstreamer-0.10/*.a"
FILES_${PN}-dbg += " ${libdir}/gstreamer-0.10/.debug/ ${libexecdir}/gstreamer-0.10/.debug/"
