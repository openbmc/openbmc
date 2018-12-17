SUMMARY = "Framework for layout and rendering of internationalized text"
DESCRIPTION = "Pango is a library for laying out and rendering of text, \
with an emphasis on internationalization. Pango can be used anywhere \
that text layout is needed, though most of the work on Pango so far has \
been done in the context of the GTK+ widget toolkit. Pango forms the \
core of text and font handling for GTK+-2.x."
HOMEPAGE = "http://www.pango.org/"
BUGTRACKER = "http://bugzilla.gnome.org"
SECTION = "libs"
LICENSE = "LGPLv2.0+"

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

inherit gnomebase gtk-doc ptest-gnome upstream-version-is-even gobject-introspection

SRC_URI += "file://run-ptest \
            file://0001-Enforce-recreation-of-docs-pango.types-it-is-build-c.patch \
"
SRC_URI[archive.md5sum] = "deb171a31a3ad76342d5195a1b5bbc7c"
SRC_URI[archive.sha256sum] = "1d2b74cd63e8bd41961f2f8d952355aa0f9be6002b52c8aa7699d9f5da597c9d"

DEPENDS = "glib-2.0 glib-2.0-native fontconfig freetype virtual/libiconv cairo harfbuzz fribidi"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "--with-xft,--without-xft,virtual/libx11 libxft"

LEAD_SONAME = "libpango-1.0*"
LIBV = "1.8.0"

# This binary needs to be compiled for the host architecture.  This isn't pretty!
do_compile_prepend_class-target () {
	if ${@bb.utils.contains('PTEST_ENABLED', '1', 'true', 'false', d)}; then
		make CC="${BUILD_CC}" CFLAGS="" LDFLAGS="${BUILD_LDFLAGS}" AM_CPPFLAGS="$(pkg-config-native --cflags glib-2.0)" gen_all_unicode_LDADD="$(pkg-config-native --libs glib-2.0)" -C ${B}/tests gen-all-unicode
	fi
}

FILES_${PN} = "${bindir}/* ${libdir}/libpango*${SOLIBS}"
FILES_${PN}-dev += "${libdir}/pango/${LIBV}/modules/*.la"

RDEPENDS_${PN}-ptest += "liberation-fonts cantarell-fonts"

RPROVIDES_${PN} += "pango-modules pango-module-indic-lang \
                    pango-module-basic-fc pango-module-arabic-lang"

BBCLASSEXTEND = "native"
