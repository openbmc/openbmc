SUMMARY = "Native icon utils for GTK+"
DESCRIPTION = "gtk-update-icon-cache and gtk-encode-symbolic-svg built from GTK+ natively, for build time and on-host postinst script execution."
SECTION = "libs"

DEPENDS = "glib-2.0-native gdk-pixbuf-native librsvg-native"

LICENSE = "LGPLv2 & LGPLv2+ & LGPLv2.1+"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtk+/${MAJ_VER}/gtk+-${PV}.tar.xz \
          file://Remove-Gdk-dependency-from-gtk-encode-symbolic-svg.patch"
SRC_URI[md5sum] = "9671acb41dc13561d19233f1a75cf184"
SRC_URI[sha256sum] = "1c53ef1bb55364698f7183ecd185b547f92f4a3a7abfafd531400232e2e052f8"

LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2 \
                    file://gtk/gtk.h;endline=25;md5=1d8dc0fccdbfa26287a271dce88af737 \
                    file://gdk/gdk.h;endline=25;md5=c920ce39dc88c6f06d3e7c50e08086f2 \
                    file://tests/testgtk.c;endline=25;md5=cb732daee1d82af7a2bf953cf3cf26f1"

S = "${WORKDIR}/gtk+-${PV}"

inherit pkgconfig native upstream-version-is-even

PKG_CONFIG_FOR_BUILD = "${STAGING_BINDIR_NATIVE}/pkg-config-native"

do_configure() {
	# Quite ugly but defines enough to compile the tools.
	if ! test -f gtk/config.h; then
		echo "#define GETTEXT_PACKAGE \"gtk30\"" >> gtk/config.h
		echo "#define HAVE_UNISTD_H 1" >> gtk/config.h
		echo "#define HAVE_FTW_H 1" >> gtk/config.h
	fi
	if ! test -f gdk/config.h; then
		touch gdk/config.h
	fi
}

do_compile() {
	${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS} \
		${S}/gtk/updateiconcache.c \
		$(${PKG_CONFIG_FOR_BUILD} --cflags --libs gdk-pixbuf-2.0) \
		-o gtk-update-icon-cache

	${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS} \
		${S}/gtk/encodesymbolic.c \
		$(${PKG_CONFIG_FOR_BUILD} --cflags --libs gio-2.0 gdk-pixbuf-2.0) \
		-o gtk-encode-symbolic-svg
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/gtk-update-icon-cache ${D}${bindir}
	install -m 0755 ${B}/gtk-encode-symbolic-svg ${D}${bindir}

	create_wrapper ${D}/${bindir}/gtk-update-icon-cache \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/2.10.0/loaders.cache
	create_wrapper ${D}/${bindir}/gtk-encode-symbolic-svg \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/2.10.0/loaders.cache
}
