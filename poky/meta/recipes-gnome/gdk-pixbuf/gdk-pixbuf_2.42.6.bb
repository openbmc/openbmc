SUMMARY = "Image loading library for GTK+"
DESCRIPTION = "The GDK Pixbuf library provides: Image loading and saving \
facilities, fast scaling and compositing of pixbufs and Simple animation \
loading (ie. animated GIFs)"
HOMEPAGE = "https://wiki.gnome.org/Projects/GdkPixbuf"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gdk-pixbuf/issues"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://gdk-pixbuf/gdk-pixbuf.h;endline=26;md5=72b39da7cbdde2e665329fef618e1d6b \
                    "

SECTION = "libs"

DEPENDS = "glib-2.0 gdk-pixbuf-native shared-mime-info"
DEPENDS:remove:class-native = "gdk-pixbuf-native"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/${BPN}/${MAJ_VER}/${BPN}-${PV}.tar.xz \
           file://run-ptest \
           file://fatal-loader.patch \
           file://0001-Add-use_prebuilt_tools-option.patch \
           "

SRC_URI[sha256sum] = "c4a6b75b7ed8f58ca48da830b9fa00ed96d668d3ab4b1f723dcf902f78bde77f"

inherit meson pkgconfig gettext pixbufcache ptest-gnome upstream-version-is-even gobject-introspection gi-docgen lib_package

GIR_MESON_OPTION = 'introspection'
GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"

LIBV = "2.10.0"

GDK_PIXBUF_LOADERS ?= "png jpeg"

PACKAGECONFIG = "${GDK_PIXBUF_LOADERS} \
                 ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG:class-native = "${GDK_PIXBUF_LOADERS}"

PACKAGECONFIG[png] = "-Dpng=true,-Dpng=false,libpng"
PACKAGECONFIG[jpeg] = "-Djpeg=true,-Djpeg=false,jpeg"
PACKAGECONFIG[tiff] = "-Dtiff=true,-Dtiff=false,tiff"
PACKAGECONFIG[tests] = "-Dinstalled_tests=true,-Dinstalled_tests=false"

EXTRA_OEMESON:class-target = " \
    -Duse_prebuilt_tools=true \
"

EXTRA_OEMESON:class-nativesdk = " \
    -Duse_prebuilt_tools=true \
"

PACKAGES =+ "${PN}-xlib"

# For GIO image type sniffing
RDEPENDS:${PN} = "shared-mime-info"

FILES:${PN}-xlib = "${libdir}/*pixbuf_xlib*${SOLIBS}"
ALLOW_EMPTY:${PN}-xlib = "1"

FILES:${PN} += "${libdir}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders"

FILES:${PN}-bin += "${datadir}/thumbnailers/gdk-pixbuf-thumbnailer.thumbnailer"

FILES:${PN}-dev += " \
	${bindir}/gdk-pixbuf-csource \
	${bindir}/gdk-pixbuf-pixdata \
        ${bindir}/gdk-pixbuf-print-mime-types \
	${includedir}/* \
	${libdir}/gdk-pixbuf-2.0/${LIBV}/loaders/*.la \
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

# Remove a bad fuzzing attempt that sporadically fails without a way to reproduce
do_install_ptest() {
	rm ${D}/${datadir}/installed-tests/gdk-pixbuf/pixbuf-randomly-modified.test
}

do_install:append:class-native() {
	find ${D}${libdir} -name "libpixbufloader-*.la" -exec rm \{\} \;

	create_wrapper ${D}/${bindir}/gdk-pixbuf-csource \
		XDG_DATA_DIRS=${STAGING_DATADIR} \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache

	create_wrapper ${D}/${bindir}/gdk-pixbuf-pixdata \
		XDG_DATA_DIRS=${STAGING_DATADIR} \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache

	create_wrapper ${D}/${bindir}/gdk-pixbuf-print-mime-types \
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
