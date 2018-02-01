SUMMARY = "Multi-platform toolkit for creating GUIs"
DESCRIPTION = "GTK+ is a multi-platform toolkit for creating graphical user interfaces. Offering a complete \
set of widgets, GTK+ is suitable for projects ranging from small one-off projects to complete application suites."
HOMEPAGE = "http://www.gtk.org"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2 & LGPLv2+ & LGPLv2.1+"

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

SECTION = "libs"

inherit distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

# This picks stable releases in the 2.x series (but not 2.90 onwards,
# which were GNOME 3 betas).
UPSTREAM_CHECK_REGEX = "(?P<pver>2\.([0-8]*[02468])+(\.\d+)+)"

X11DEPENDS = "virtual/libx11 libxext libxcursor libxrandr libxdamage libxrender libxcomposite"
DEPENDS = "glib-2.0 pango atk jpeg libpng gdk-pixbuf-native \
 cairo gdk-pixbuf"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'directfb x11', d)}"

PACKAGECONFIG[x11] = "--with-x=yes --with-gdktarget=x11,--with-x=no,${X11DEPENDS}"
# without --with-gdktarget=directfb it will check for cairo-xlib which isn't available without X11 DISTRO_FEATURE
PACKAGECONFIG[directfb] = "--with-gdktarget=directfb,,directfb"
PACKAGECONFIG[manpages] = "--enable-man --with-xml-catalog=${STAGING_ETCDIR_NATIVE}/xml/catalog.xml, --disable-man, libxslt-native xmlto-native"

inherit autotools gtk-doc pkgconfig update-alternatives gtk-immodules-cache gobject-introspection manpages

PACKAGES += "libgail gtk-demo"

FILES_${PN} += "${bindir}/gtk-update-icon-cache-2.0 \
	${bindir}/gtk-query-immodules-2.0 \
	${datadir}/themes ${sysconfdir} \
	${libdir}/gtk-2.0/${LIBV}/engines/libpixmap.so"

FILES_${PN}-dev += " \
        ${datadir}/gtk-2.0/include \
	${libdir}/gtk-2.0/include \
	${libdir}/gtk-2.0/modules/*.la \
	${libdir}/gtk-2.0/${LIBV}/loaders/*.la \
	${libdir}/gtk-2.0/${LIBV}/immodules/*.la \
	${libdir}/gtk-2.0/${LIBV}/printbackends/*.la \
	${libdir}/gtk-2.0/${LIBV}/engines/*.la \
	${bindir}/gtk-builder-convert"

FILES_gtk-demo = " \
 ${datadir}/gtk-2.0/demo/* \
 ${bindir}/gtk-demo \
 "

FILES_libgail = " \
	${libdir}/gtk-2.0/modules/libgail.so \
	${libdir}/gtk-2.0/modules/libferret.so \
	"

GTKBASE_RRECOMMENDS ?= "liberation-fonts \
                        gdk-pixbuf-loader-png \
                        gdk-pixbuf-loader-jpeg \
                        gdk-pixbuf-loader-gif \
                        gdk-pixbuf-loader-xpm \
                        shared-mime-info \
                        gnome-theme-adwaita \
                        "
GTKGLIBC_RRECOMMENDS ?= "${GTKBASE_RRECOMMENDS} glibc-gconv-iso8859-1"

RRECOMMENDS_${PN} = "${GTKBASE_RRECOMMENDS}"
RRECOMMENDS_${PN}_libc-glibc = "${GTKGLIBC_RRECOMMENDS}"

ALTERNATIVE_${PN} = "gtk-update-icon-cache"
ALTERNATIVE_TARGET[gtk-update-icon-cache] = "${bindir}/gtk-update-icon-cache-2.0"

do_compile_prepend() {
        export GIR_EXTRA_LIBS_PATH="${B}/gdk/.libs"
}

do_install () {
	autotools_do_install

	install -d ${D}${sysconfdir}/gtk-2.0

	mkdir -p ${D}${libdir}/gtk-2.0/include
	install -m 0644 gdk/gdkconfig.h ${D}${libdir}/gtk-2.0/include/gdkconfig.h

	install -m 0644 ${S}/gtk/gtkfilechooserprivate.h ${D}${includedir}/gtk-2.0/gtk/
	install -m 0644 ${S}/gtk/gtkfilechooserutils.h   ${D}${includedir}/gtk-2.0/gtk/
	install -m 0644 ${S}/gtk/gtkfilesystemmodel.h    ${D}${includedir}/gtk-2.0/gtk/

	mv ${D}${bindir}/gtk-update-icon-cache ${D}${bindir}/gtk-update-icon-cache-2.0
}

SYSROOT_PREPROCESS_FUNCS += "gtk_sysroot_preprocess"

gtk_sysroot_preprocess () {
	if [ -e ${D}${bindir}/gtk-builder-convert ]; then
		install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
		install -m 755 ${D}${bindir}/gtk-builder-convert ${SYSROOT_DESTDIR}${bindir_crossscripts}/
	fi
}

