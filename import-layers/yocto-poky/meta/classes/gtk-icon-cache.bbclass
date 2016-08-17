FILES_${PN} += "${datadir}/icons/hicolor"

DEPENDS += "${@['hicolor-icon-theme', '']['${BPN}' == 'hicolor-icon-theme']} gtk-icon-utils-native"

gtk_icon_cache_postinst() {
if [ "x$D" != "x" ]; then
	$INTERCEPT_DIR/postinst_intercept update_icon_cache ${PKG} \
		mlprefix=${MLPREFIX} \
		libdir_native=${libdir_native}
else

	# Update the pixbuf loaders in case they haven't been registered yet
	${libdir}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders --update-cache

	for icondir in /usr/share/icons/* ; do
		if [ -d $icondir ] ; then
			gtk-update-icon-cache -fqt  $icondir
		fi
	done
fi
}

gtk_icon_cache_postrm() {
if [ "x$D" != "x" ]; then
	$INTERCEPT_DIR/postinst_intercept update_icon_cache ${PKG} \
		mlprefix=${MLPREFIX} \
		libdir=${libdir}
else
	for icondir in /usr/share/icons/* ; do
		if [ -d $icondir ] ; then
			gtk-update-icon-cache -qt  $icondir
		fi
	done
fi
}

python populate_packages_append () {
    packages = d.getVar('PACKAGES', True).split()
    pkgdest =  d.getVar('PKGDEST', True)
    
    for pkg in packages:
        icon_dir = '%s/%s/%s/icons' % (pkgdest, pkg, d.getVar('datadir', True))
        if not os.path.exists(icon_dir):
            continue

        bb.note("adding hicolor-icon-theme dependency to %s" % pkg)
        rdepends = ' ' + d.getVar('MLPREFIX', False) + "hicolor-icon-theme"
        d.appendVar('RDEPENDS_%s' % pkg, rdepends)
    
        bb.note("adding gtk-icon-cache postinst and postrm scripts to %s" % pkg)
        
        postinst = d.getVar('pkg_postinst_%s' % pkg, True)
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += d.getVar('gtk_icon_cache_postinst', True)
        d.setVar('pkg_postinst_%s' % pkg, postinst)

        postrm = d.getVar('pkg_postrm_%s' % pkg, True)
        if not postrm:
            postrm = '#!/bin/sh\n'
        postrm += d.getVar('gtk_icon_cache_postrm', True)
        d.setVar('pkg_postrm_%s' % pkg, postrm)
}

