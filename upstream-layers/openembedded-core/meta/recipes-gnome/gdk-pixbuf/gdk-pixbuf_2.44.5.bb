SUMMARY = "Image loading library for GTK+"
DESCRIPTION = "The GDK Pixbuf library provides: Image loading and saving \
facilities, fast scaling and compositing of pixbufs and Simple animation \
loading (ie. animated GIFs)"
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gdk-pixbuf"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gdk-pixbuf/issues"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://gdk-pixbuf/gdk-pixbuf.h;endline=26;md5=72b39da7cbdde2e665329fef618e1d6b \
                    "

SECTION = "libs"

DEPENDS = "glib-2.0 shared-mime-info"

SRC_URI[archive.sha256sum] = "69b93e09139b80c0ee661503d60deb5a5874a31772b5184b9cd5462a4100ab68"

inherit gettext gnomebase pixbufcache ptest-gnome upstream-version-is-even gobject-introspection gi-docgen lib_package manpages

SRC_URI += "\
           file://run-ptest \
           file://fatal-loader.patch \
           file://0001-meson.build-allow-a-subset-of-tests-in-cross-compile.patch \
           "

GIR_MESON_OPTION = "introspection"
GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"
GIDOCGEN_MESON_OPTION = "documentation"

LIBV = "2.10.0"

PACKAGECONFIG ??= "png jpeg gif others \
                   ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"

PACKAGECONFIG[png] = "-Dpng=enabled,-Dpng=disabled,libpng"
PACKAGECONFIG[jpeg] = "-Djpeg=enabled,-Djpeg=disabled,jpeg"
PACKAGECONFIG[tiff] = "-Dtiff=enabled,-Dtiff=disabled,tiff"
PACKAGECONFIG[gif] = "-Dgif=enabled,-Dgif=disabled"
PACKAGECONFIG[others] = "-Dothers=enabled,-Dothers=disabled"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,python3-docutils-native"
PACKAGECONFIG[tests] = "-Dtests=true -Dinstalled_tests=true,-Dtests=false -Dinstalled_tests=false"

# Disable glycin whilst we have no recipe yet
# Disable the thumbnailers as they don't build in cross
EXTRA_OEMESON = "-Dglycin=disabled -Dthumbnailer=disabled"

# For GIO image type sniffing
RDEPENDS:${PN} = "shared-mime-info"

FILES:${PN} += "${libdir}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders"

FILES:${PN}-bin += "${datadir}/thumbnailers/gdk-pixbuf-thumbnailer.thumbnailer"

FILES:${PN}-dev += " \
	${bindir}/gdk-pixbuf-csource \
	${bindir}/gdk-pixbuf-pixdata \
	${bindir}/gdk-pixbuf-print-mime-types \
	${includedir}/* \
"

PACKAGES_DYNAMIC += "^gdk-pixbuf-loader-.*"
PACKAGES_DYNAMIC:class-native = ""

python populate_packages:prepend () {
    postinst_pixbufloader = d.getVar("postinst_pixbufloader")

    loaders_root = d.expand('${libdir}/gdk-pixbuf-2.0/${LIBV}/loaders')

    packages = ' '.join(do_split_packages(d, loaders_root, r'^libpixbufloader-(.*)\.so$', 'gdk-pixbuf-loader-%s', 'GDK pixbuf loader for %s'))
    d.setVar('PIXBUF_PACKAGES', packages)

    # The test suite exercises all the loaders, so ensure they are all
    # dependencies of the ptest package.
    d.appendVar("RDEPENDS:%s-ptest" % d.getVar('PN'), " " + packages)
}

do_install:append() {
	# Copy gdk-pixbuf-query-loaders into libdir so it is always available
	# in multilib builds.
	cp ${D}/${bindir}/gdk-pixbuf-query-loaders ${D}/${libdir}/gdk-pixbuf-2.0/

}

do_install_ptest() {
	# Remove a bad fuzzing attempt that sporadically fails without a way to reproduce
	rm ${D}/${datadir}/installed-tests/gdk-pixbuf/pixbuf-randomly-modified.test
	# https://gitlab.gnome.org/GNOME/gdk-pixbuf/-/issues/215
	rm ${D}/${datadir}/installed-tests/gdk-pixbuf/pixbuf-jpeg.test
}

do_install:append:class-native() {
	create_wrapper ${D}/${bindir}/gdk-pixbuf-csource \
		XDG_DATA_DIRS=${STAGING_DATADIR} \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache

	create_wrapper ${D}/${bindir}/gdk-pixbuf-pixdata \
		XDG_DATA_DIRS=${STAGING_DATADIR} \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache

	create_wrapper ${D}/${libdir}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders \
		XDG_DATA_DIRS=${STAGING_DATADIR} \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache \
		GDK_PIXBUF_MODULEDIR=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders

	create_wrapper ${D}/${bindir}/gdk-pixbuf-query-loaders \
		XDG_DATA_DIRS=${STAGING_DATADIR} \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache \
		GDK_PIXBUF_MODULEDIR=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders
}
BBCLASSEXTEND = "native nativesdk"
