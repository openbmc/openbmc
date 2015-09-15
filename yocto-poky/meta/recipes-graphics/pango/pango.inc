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

X11DEPENDS = "virtual/libx11 libxft"
DEPENDS = "glib-2.0 fontconfig freetype zlib virtual/libiconv cairo harfbuzz qemu-native"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'ptest', '', d)}"

PACKAGECONFIG[x11] = "--with-xft,--without-xft,${X11DEPENDS}"
PACKAGECONFIG[ptest] = "--enable-installed-tests,--disable-installed-tests,glib-2.0-native"

BBCLASSEXTEND = "native"
DEPENDS_class-native = "glib-2.0-native cairo-native harfbuzz-native"

PACKAGES_DYNAMIC += "^pango-module-.*"

RRECOMMENDS_${PN} = "pango-module-basic-fc"

inherit gnomebase gtk-doc qemu ptest

# Create a pango-modules package
ALLOW_EMPTY_${BPN}-modules = "1"
PACKAGES += "${BPN}-modules"
RRECOMMENDS_${BPN}-modules =  "${@" ".join([p for p in d.getVar('PACKAGES', True).split() if p.find("pango-module") != -1])}"


EXTRA_AUTORECONF = ""

# seems to go wrong with default cflags
FULL_OPTIMIZATION_arm = "-O2"

EXTRA_OECONF = "--disable-introspection \
		--enable-explicit-deps=no \
	        --disable-debug \
	        --with-mlprefix=${MLPREFIX}"

LEAD_SONAME = "libpango-1.0*"
LIBV = "1.8.0"

pango_postinst() {
if ! [ -e $D${sysconfdir}/pango ] ; then
	mkdir -p $D${sysconfdir}/pango
fi

if [ -n "$D" ]; then
	${@qemu_run_binary(d, '$D','${bindir}/${MLPREFIX}pango-querymodules')} \
		$D${libdir}/pango/${LIBV}/modules/*.so \
		> $D${sysconfdir}/pango/${MLPREFIX}pango.modules 2>/dev/null

	[ $? -ne 0 ] && exit 1

	sed -i -e "s:$D::" $D${sysconfdir}/pango/${MLPREFIX}pango.modules

	exit 0
else
	${bindir}/${MLPREFIX}pango-querymodules > /etc/pango/${MLPREFIX}pango.modules
fi
}

# This binary needs to be compiled for the host architecture.  This isn't pretty!
do_compile_prepend () {
	if ${@base_contains('DISTRO_FEATURES', 'ptest', 'true', 'false', d)}; then
		make CC="${BUILD_CC}" CFLAGS="" AM_CPPFLAGS="$(pkg-config-native --cflags glib-2.0)" gen_all_unicode_LDADD="$(pkg-config-native --libs glib-2.0)" -C ${B}/tests gen-all-unicode
	fi
}

do_install_append () {
	if [ "${MLPREFIX}" != "" ]; then
		mv ${D}/${bindir}/pango-querymodules ${D}/${bindir}/${MLPREFIX}pango-querymodules 
	fi
}


python populate_packages_prepend () {
    pango_postinst = d.getVar("pango_postinst", True)

    modules_root = d.expand('${libdir}/pango/${LIBV}/modules')

    do_split_packages(d, modules_root, '^pango-(.*)\.so$', 'pango-module-%s', 'Pango module %s', pango_postinst)
}

FILES_${PN} = "${sysconfdir}/pango/* ${bindir}/* ${libdir}/libpango*${SOLIBS}"
FILES_${PN}-dbg += "${libdir}/pango/${LIBV}/modules/.debug"
FILES_${PN}-dev += "${libdir}/pango/${LIBV}/modules/*.la"

FILES_${PN}-ptest += "${libexecdir}/installed-tests/* \
                      ${datadir}/installed-tests/pango"
FILES_${PN}-dbg += "${libexecdir}/installed-tests/.debug"

RDEPENDS_${PN}-ptest += "gnome-desktop-testing liberation-fonts"
