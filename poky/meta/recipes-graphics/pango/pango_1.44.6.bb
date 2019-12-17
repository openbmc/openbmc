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
            file://0001-Skip-thai-break-tests-without-libthai.patch"
SRC_URI[archive.md5sum] = "db0a3243ba33e02aaa775412f8e5f412"
SRC_URI[archive.sha256sum] = "3e1e41ba838737e200611ff001e3b304c2ca4cdbba63d200a20db0b0ddc0f86c"

DEPENDS = "glib-2.0 glib-2.0-native fontconfig freetype virtual/libiconv cairo harfbuzz fribidi"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} \
                   ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"

PACKAGECONFIG[x11] = ",,virtual/libx11 libxft"
PACKAGECONFIG[tests] = "-Dinstall-tests=true, -Dinstall-tests=false"
PACKAGECONFIG[thai] = ",,libthai"

GTKDOC_MESON_OPTION = "gtk_doc"
GIR_MESON_OPTION = 'introspection'

LEAD_SONAME = "libpango-1.0*"

FILES_${PN} = "${bindir}/* ${libdir}/libpango*${SOLIBS}"

RDEPENDS_${PN}-ptest += "cantarell-fonts"
RDEPENDS_${PN}-ptest_append_libc-glibc = " locale-base-en-us"

RPROVIDES_${PN} += "pango-modules pango-module-indic-lang \
                    pango-module-basic-fc pango-module-arabic-lang"

BBCLASSEXTEND = "native nativesdk"
