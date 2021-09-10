#
# This class will generate the proper postinst/postrm scriptlets for pixbuf
# packages.
#

DEPENDS:append:class-target = " qemu-native"
inherit qemu

PIXBUF_PACKAGES ??= "${PN}"

PACKAGE_WRITE_DEPS += "qemu-native gdk-pixbuf-native"

pixbufcache_common() {
if [ "x$D" != "x" ]; then
	$INTERCEPT_DIR/postinst_intercept update_pixbuf_cache ${PKG} mlprefix=${MLPREFIX} binprefix=${MLPREFIX} libdir=${libdir} \
		bindir=${bindir} base_libdir=${base_libdir}
else

	# Update the pixbuf loaders in case they haven't been registered yet
	${libdir}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders --update-cache

	if [ -x ${bindir}/gtk-update-icon-cache ] && [ -d ${datadir}/icons ]; then
		for icondir in /usr/share/icons/*; do
			if [ -d ${icondir} ]; then
				gtk-update-icon-cache -t -q ${icondir}
			fi
		done
	fi
fi
}

python populate_packages:append() {
    pixbuf_pkgs = d.getVar('PIXBUF_PACKAGES').split()

    for pkg in pixbuf_pkgs:
        bb.note("adding pixbuf postinst and postrm scripts to %s" % pkg)
        postinst = d.getVar('pkg_postinst:%s' % pkg) or d.getVar('pkg_postinst')
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += d.getVar('pixbufcache_common')
        d.setVar('pkg_postinst:%s' % pkg, postinst)

        postrm = d.getVar('pkg_postrm:%s' % pkg) or d.getVar('pkg_postrm')
        if not postrm:
            postrm = '#!/bin/sh\n'
        postrm += d.getVar('pixbufcache_common')
        d.setVar('pkg_postrm:%s' % pkg, postrm)
}

gdkpixbuf_complete() {
GDK_PIXBUF_FATAL_LOADER=1 ${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders --update-cache || exit 1
}

DEPENDS:append:class-native = " gdk-pixbuf-native"
SYSROOT_PREPROCESS_FUNCS:append:class-native = " pixbufcache_sstate_postinst"

pixbufcache_sstate_postinst() {
	mkdir -p ${SYSROOT_DESTDIR}${bindir}
	dest=${SYSROOT_DESTDIR}${bindir}/postinst-${PN}
        echo '#!/bin/sh' > $dest
	echo "${gdkpixbuf_complete}" >> $dest
	chmod 0755 $dest
}
