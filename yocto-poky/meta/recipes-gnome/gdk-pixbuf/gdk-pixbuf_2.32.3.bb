SUMMARY = "Image loading library for GTK+"
HOMEPAGE = "http://www.gtk.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://gdk-pixbuf/gdk-pixbuf.h;endline=26;md5=72b39da7cbdde2e665329fef618e1d6b"

SECTION = "libs"

DEPENDS = "glib-2.0"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/${BPN}/${MAJ_VER}/${BPN}-${PV}.tar.xz \
           file://hardcoded_libtool.patch \
           file://extending-libinstall-dependencies.patch \
           file://run-ptest \
           file://fatal-loader.patch \
           "

SRC_URI[md5sum] = "9275076639baa24a342f3e02c402920e"
SRC_URI[sha256sum] = "2b6771f1ac72f687a8971e59810b8dc658e65e7d3086bd2e676e618fd541d031"

inherit autotools pkgconfig gettext pixbufcache ptest-gnome upstream-version-is-even gobject-introspection

LIBV = "2.10.0"

GDK_PIXBUF_LOADERS ?= "png jpeg"

PACKAGECONFIG ??= "${GDK_PIXBUF_LOADERS}"
PACKAGECONFIG_linuxstdbase = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} ${GDK_PIXBUF_LOADERS}"
PACKAGECONFIG_class-native = "${GDK_PIXBUF_LOADERS}"

PACKAGECONFIG[png] = "--with-libpng,--without-libpng,libpng"
PACKAGECONFIG[jpeg] = "--with-libjpeg,--without-libjpeg,jpeg"
PACKAGECONFIG[tiff] = "--with-libtiff,--without-libtiff,tiff"
PACKAGECONFIG[jpeg2000] = "--with-libjasper,--without-libjasper,jasper"

# Use GIO to sniff image format instead of trying all loaders
PACKAGECONFIG[gio-sniff] = "--enable-gio-sniffing,--disable-gio-sniffing,,shared-mime-info"
PACKAGECONFIG[x11] = "--with-x11,--without-x11,virtual/libx11"

PACKAGES =+ "${PN}-xlib"

FILES_${PN}-xlib = "${libdir}/*pixbuf_xlib*${SOLIBS}"
ALLOW_EMPTY_${PN}-xlib = "1"

FILES_${PN} += "${libdir}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders"

FILES_${PN}-dev += " \
	${bindir}/gdk-pixbuf-csource \
	${bindir}/gdk-pixbuf-pixdata \
	${includedir}/* \
	${libdir}/gdk-pixbuf-2.0/${LIBV}/loaders/*.la \
"

PACKAGES_DYNAMIC += "^gdk-pixbuf-loader-.*"
PACKAGES_DYNAMIC_class-native = ""

python populate_packages_prepend () {
    postinst_pixbufloader = d.getVar("postinst_pixbufloader", True)

    loaders_root = d.expand('${libdir}/gdk-pixbuf-2.0/${LIBV}/loaders')

    packages = ' '.join(do_split_packages(d, loaders_root, '^libpixbufloader-(.*)\.so$', 'gdk-pixbuf-loader-%s', 'GDK pixbuf loader for %s'))
    d.setVar('PIXBUF_PACKAGES', packages)

    # The test suite exercises all the loaders, so ensure they are all
    # dependencies of the ptest package.
    d.appendVar("RDEPENDS_gdk-pixbuf-ptest", " " + packages)
}

do_install_append() {
	# Move gdk-pixbuf-query-loaders into libdir so it is always available
	# in multilib builds.
	mv ${D}/${bindir}/gdk-pixbuf-query-loaders ${D}/${libdir}/gdk-pixbuf-2.0/
}

do_install_append_class-native() {
	find ${D}${libdir} -name "libpixbufloader-*.la" -exec rm \{\} \;

	create_wrapper ${D}/${bindir}/gdk-pixbuf-csource \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache

	create_wrapper ${D}/${bindir}/gdk-pixbuf-pixdata \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache

	create_wrapper ${D}/${libdir}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache \
		GDK_PIXBUF_MODULEDIR=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders
}
BBCLASSEXTEND = "native"

SSTATEPREINSTFUNCS_append_class-native = " gdkpixbuf_sstate_preinst"
SYSROOT_PREPROCESS_FUNCS_append_class-native = " gdkpixbuf_sstate_preinst"

gdkpixbuf_sstate_preinst() {
	if [ "${BB_CURRENTTASK}" = "populate_sysroot" ]; then
		rm -rf ${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/*
	fi
}
