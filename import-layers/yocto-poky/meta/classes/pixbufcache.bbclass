#
# This class will generate the proper postinst/postrm scriptlets for pixbuf
# packages.
#

DEPENDS += "qemu-native"
inherit qemu

PIXBUF_PACKAGES ??= "${PN}"

pixbufcache_common() {
if [ "x$D" != "x" ]; then
	$INTERCEPT_DIR/postinst_intercept update_pixbuf_cache ${PKG} mlprefix=${MLPREFIX} libdir=${libdir} \
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

python populate_packages_append() {
    pixbuf_pkgs = d.getVar('PIXBUF_PACKAGES', True).split()

    for pkg in pixbuf_pkgs:
        bb.note("adding pixbuf postinst and postrm scripts to %s" % pkg)
        postinst = d.getVar('pkg_postinst_%s' % pkg, True) or d.getVar('pkg_postinst', True)
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += d.getVar('pixbufcache_common', True)
        d.setVar('pkg_postinst_%s' % pkg, postinst)

        postrm = d.getVar('pkg_postrm_%s' % pkg, True) or d.getVar('pkg_postrm', True)
        if not postrm:
            postrm = '#!/bin/sh\n'
        postrm += d.getVar('pixbufcache_common', True)
        d.setVar('pkg_postrm_%s' % pkg, postrm)
}

gdkpixbuf_complete() {
	GDK_PIXBUF_FATAL_LOADER=1 ${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders --update-cache || exit 1
}

#
# Add an sstate postinst hook to update the cache for native packages.
# An error exit during populate_sysroot_setscene allows bitbake to
# try to recover by re-building the package.
#
SSTATEPOSTINSTFUNCS_append_class-native = " pixbufcache_sstate_postinst"

# See base.bbclass for the other half of this
pixbufcache_sstate_postinst() {
	if [ "${BB_CURRENTTASK}" = "populate_sysroot" ]; then
		${gdkpixbuf_complete}
	elif [ "${BB_CURRENTTASK}" = "populate_sysroot_setscene" ]; then
		echo "${gdkpixbuf_complete}" >> ${STAGING_DIR}/sstatecompletions
	fi
}

