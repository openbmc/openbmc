SUMMARY = "Image loading library for GTK+"
DESCRIPTION = "The GDK Pixbuf library provides: Image loading and saving \
facilities, fast scaling and compositing of pixbufs and Simple animation \
loading (ie. animated GIFs)"
HOMEPAGE = "https://wiki.gnome.org/Projects/GdkPixbuf"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/gdk-pixbuf/issues"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://gdk-pixbuf/gdk-pixbuf.h;endline=26;md5=72b39da7cbdde2e665329fef618e1d6b \
                    "

SECTION = "libs"

DEPENDS = "glib-2.0 gdk-pixbuf-native shared-mime-info"
DEPENDS_remove_class-native = "gdk-pixbuf-native"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/${BPN}/${MAJ_VER}/${BPN}-${PV}.tar.xz \
           file://run-ptest \
           file://fatal-loader.patch \
           file://0001-Work-around-thumbnailer-cross-compile-failure.patch \
           file://0001-Fix-a-couple-of-decisions-around-cross-compilation.patch \
           file://0004-Do-not-run-tests-when-building.patch \
           file://0006-Build-thumbnailer-and-tests-also-in-cross-builds.patch \
           file://missing-test-data.patch \
           file://CVE-2020-29385.patch \
           "

SRC_URI_append_class-target = " \
           file://0003-target-only-Work-around-thumbnailer-cross-compile-fa.patch \
           "
SRC_URI_append_class-nativesdk = " \
           file://0003-target-only-Work-around-thumbnailer-cross-compile-fa.patch \
           "

SRC_URI[md5sum] = "05eb1ebc258ba905f1c8644ef49de064"
SRC_URI[sha256sum] = "1582595099537ca8ff3b99c6804350b4c058bb8ad67411bbaae024ee7cead4e6"

inherit meson pkgconfig gettext pixbufcache ptest-gnome upstream-version-is-even gobject-introspection gtk-doc lib_package

GIR_MESON_OPTION = 'gir'

LIBV = "2.10.0"

GDK_PIXBUF_LOADERS ?= "png jpeg"

PACKAGECONFIG = "${GDK_PIXBUF_LOADERS} \
                 ${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} \
                 ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG_class-native = "${GDK_PIXBUF_LOADERS}"

PACKAGECONFIG[png] = "-Dpng=true,-Dpng=false,libpng"
PACKAGECONFIG[jpeg] = "-Djpeg=true,-Djpeg=false,jpeg"
PACKAGECONFIG[tiff] = "-Dtiff=true,-Dtiff=false,tiff"
PACKAGECONFIG[jpeg2000] = "-Djasper=true,-Djasper=false,jasper"
PACKAGECONFIG[tests] = "-Dinstalled_tests=true,-Dinstalled_tests=false"
PACKAGECONFIG[x11] = "-Dx11=true,-Dx11=false,virtual/libx11"

PACKAGES =+ "${PN}-xlib"

# For GIO image type sniffing
RDEPENDS_${PN} = "shared-mime-info"

FILES_${PN}-xlib = "${libdir}/*pixbuf_xlib*${SOLIBS}"
ALLOW_EMPTY_${PN}-xlib = "1"

FILES_${PN} += "${libdir}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders"

FILES_${PN}-bin += "${datadir}/thumbnailers/gdk-pixbuf-thumbnailer.thumbnailer"

FILES_${PN}-dev += " \
	${bindir}/gdk-pixbuf-csource \
	${bindir}/gdk-pixbuf-pixdata \
        ${bindir}/gdk-pixbuf-print-mime-types \
	${includedir}/* \
	${libdir}/gdk-pixbuf-2.0/${LIBV}/loaders/*.la \
"

PACKAGES_DYNAMIC += "^gdk-pixbuf-loader-.*"
PACKAGES_DYNAMIC_class-native = ""

python populate_packages_prepend () {
    postinst_pixbufloader = d.getVar("postinst_pixbufloader")

    loaders_root = d.expand('${libdir}/gdk-pixbuf-2.0/${LIBV}/loaders')

    packages = ' '.join(do_split_packages(d, loaders_root, r'^libpixbufloader-(.*)\.so$', 'gdk-pixbuf-loader-%s', 'GDK pixbuf loader for %s'))
    d.setVar('PIXBUF_PACKAGES', packages)

    # The test suite exercises all the loaders, so ensure they are all
    # dependencies of the ptest package.
    d.appendVar("RDEPENDS_%s-ptest" % d.getVar('PN'), " " + packages)
}

do_install_append() {
	# Copy gdk-pixbuf-query-loaders into libdir so it is always available
	# in multilib builds.
	cp ${D}/${bindir}/gdk-pixbuf-query-loaders ${D}/${libdir}/gdk-pixbuf-2.0/

}

# Remove a bad fuzzing attempt that sporadically fails without a way to reproduce
do_install_ptest() {
	rm ${D}/${datadir}/installed-tests/gdk-pixbuf/pixbuf-randomly-modified.test
}

do_install_append_class-native() {
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
