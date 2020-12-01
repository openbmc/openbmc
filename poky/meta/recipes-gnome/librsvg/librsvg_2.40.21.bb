SUMMARY = "Library for rendering SVG files"
DESCRIPTION = "A small library to render Scalable Vector Graphics (SVG), \
associated with the GNOME Project. It renders SVG files to Cairo surfaces. \
Cairo is the 2D, antialiased drawing library that GNOME uses to draw things to \
the screen or to generate output for printing."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/librsvg"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/librsvg/issues"

RECIPE_NO_UPDATE_REASON = "Versions from 2.41.0 requires Rust compiler to build it"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://rsvg.h;beginline=3;endline=24;md5=20b4113c4909bbf0d67e006778302bc6"

SECTION = "x11/utils"
DEPENDS = "cairo gdk-pixbuf glib-2.0 libcroco libxml2 pango"
BBCLASSEXTEND = "native"

inherit gnomebase gtk-doc pixbufcache upstream-version-is-even gobject-introspection

SRC_URI += "file://gtk-option.patch \
            file://0001-Auto-detect-Bsymbolic-fixes-configure-on-macOS.patch \
            file://0001-Remove-non-reproducible-SRCDIR.patch \
"

SRC_URI[archive.sha256sum] = "f7628905f1cada84e87e2b14883ed57d8094dca3281d5bcb24ece4279e9a92ba"

CACHED_CONFIGUREVARS = "ac_cv_path_GDK_PIXBUF_QUERYLOADERS=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders"

PACKAGECONFIG ??= "gdkpixbuf"
# The gdk-pixbuf loader
PACKAGECONFIG[gdkpixbuf] = "--enable-pixbuf-loader,--disable-pixbuf-loader,gdk-pixbuf-native"
# GTK+ test application (rsvg-view)
PACKAGECONFIG[gtk] = "--with-gtk3,--without-gtk3,gtk+3"

do_install_append() {
	# Loadable modules don't need .a or .la on Linux
	rm -f ${D}${libdir}/gdk-pixbuf-2.0/*/loaders/*.a ${D}${libdir}/gdk-pixbuf-2.0/*/loaders/*.la
}

PACKAGES =+ "librsvg-gtk rsvg"
FILES_rsvg = "${bindir}/rsvg* \
	      ${datadir}/pixmaps/svg-viewer.svg \
	      ${datadir}/themes"
FILES_librsvg-gtk = "${libdir}/gdk-pixbuf-2.0/*/*/*.so \
                     ${datadir}/thumbnailers/librsvg.thumbnailer"
RRECOMMENDS_librsvg-gtk = "gdk-pixbuf-bin"

PIXBUF_PACKAGES = "librsvg-gtk"
