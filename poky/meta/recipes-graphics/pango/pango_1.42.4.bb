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

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gtk-doc ptest-gnome upstream-version-is-even gobject-introspection

SRC_URI += "file://run-ptest \
            file://insensitive-diff.patch"

SRC_URI[archive.md5sum] = "deb171a31a3ad76342d5195a1b5bbc7c"
SRC_URI[archive.sha256sum] = "1d2b74cd63e8bd41961f2f8d952355aa0f9be6002b52c8aa7699d9f5da597c9d"

DEPENDS = "glib-2.0 glib-2.0-native fontconfig freetype virtual/libiconv cairo harfbuzz fribidi"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = ",,virtual/libx11 libxft"

GTKDOC_ENABLE_FLAG = "-Denable_docs=true"
GTKDOC_DISABLE_FLAG = "-Denable_docs=false"

GI_ENABLE_FLAG = "-Dgir=true"
GI_DISABLE_FLAG = "-Dgir=false"

EXTRA_OEMESON_append_class-nativesdk = " ${GI_DISABLE_FLAG}"

EXTRA_OEMESON_append_class-target = " ${@bb.utils.contains('GI_DATA_ENABLED', 'True', '${GI_ENABLE_FLAG}', \
                                                                                       '${GI_DISABLE_FLAG}', d)} "

EXTRA_OEMESON_append_class-target = " ${@bb.utils.contains('GTKDOC_ENABLED', 'True', '${GTKDOC_ENABLE_FLAG}', \
                                                                                     '${GTKDOC_DISABLE_FLAG}', d)} "

LEAD_SONAME = "libpango-1.0*"
LIBV = "1.8.0"

FILES_${PN} = "${bindir}/* ${libdir}/libpango*${SOLIBS}"
FILES_${PN}-dev += "${libdir}/pango/${LIBV}/modules/*.la"

RDEPENDS_${PN}-ptest += "liberation-fonts cantarell-fonts"

RPROVIDES_${PN} += "pango-modules pango-module-indic-lang \
                    pango-module-basic-fc pango-module-arabic-lang"

BBCLASSEXTEND = "native nativesdk"

do_install_append () {
	if [ "${PTEST_ENABLED}" != "1" ]; then
		rm -rf ${D}${libexecdir}/installed-tests ${D}${datadir}/installed-tests
                rmdir --ignore-fail-on-non-empty ${D}${libexecdir} ${D}${datadir}
	fi
}
