SUMMARY = "Framework for layout and rendering of internationalized text"
DESCRIPTION = "Pango is a library for laying out and rendering of text, \
with an emphasis on internationalization. Pango can be used anywhere \
that text layout is needed, though most of the work on Pango so far has \
been done in the context of the GTK+ widget toolkit. Pango forms the \
core of text and font handling for GTK+-2.x."
HOMEPAGE = "http://www.pango.org/"
BUGTRACKER = "http://bugzilla.gnome.org"
SECTION = "libs"
LICENSE = "LGPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"


inherit gnomebase gi-docgen ptest-gnome upstream-version-is-even gobject-introspection

UPSTREAM_CHECK_REGEX = "pango-(?P<pver>\d+\.(?!9\d+)\d+\.\d+)"

GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"

SRC_URI += "file://run-ptest \
           file://0001-Skip-running-test-layout-test.patch \
           "

SRC_URI[archive.sha256sum] = "58728a0a2d86f60761208df9493033d18ecb2497abac80ee1a274ad0c6e55f0f"

DEPENDS = "glib-2.0 glib-2.0-native fontconfig freetype virtual/libiconv cairo harfbuzz fribidi"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} \
                   ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"

PACKAGECONFIG[x11] = ",,virtual/libx11 libxft"
PACKAGECONFIG[tests] = "-Dinstall-tests=true, -Dinstall-tests=false"
PACKAGECONFIG[thai] = "-Dlibthai=enabled,-Dlibthai=disabled,libthai"

GIR_MESON_OPTION = 'introspection'

do_configure:prepend() {
	chmod +x ${S}/tests/*.py
}

LEAD_SONAME = "libpango-1.0*"

FILES:${PN} = "${bindir}/* ${libdir}/libpango*${SOLIBS}"

RDEPENDS:${PN}-ptest += "cantarell-fonts"
RDEPENDS:${PN}-ptest:append:libc-glibc = " locale-base-en-us"

RPROVIDES:${PN} += "pango-modules pango-module-indic-lang \
                    pango-module-basic-fc pango-module-arabic-lang"

BBCLASSEXTEND = "native nativesdk"
