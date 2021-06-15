FILES_${PN} += "${datadir}/icons/hicolor"

#gtk+3 reqiure GTK3DISTROFEATURES, DEPENDS on it make all the
#recipes inherit this class require GTK3DISTROFEATURES
inherit features_check
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS +=" ${@['hicolor-icon-theme', '']['${BPN}' == 'hicolor-icon-theme']} \
            ${@['gdk-pixbuf', '']['${BPN}' == 'gdk-pixbuf']} \
            ${@['gtk+3', '']['${BPN}' == 'gtk+3']} \
            gtk+3-native \
"

PACKAGE_WRITE_DEPS += "gtk+3-native gdk-pixbuf-native"

gtk_icon_cache_postinst() {
if [ "x$D" != "x" ]; then
	$INTERCEPT_DIR/postinst_intercept update_gtk_icon_cache ${PKG} \
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
	$INTERCEPT_DIR/postinst_intercept update_gtk_icon_cache ${PKG} \
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
    packages = d.getVar('PACKAGES').split()
    pkgdest =  d.getVar('PKGDEST')
    
    for pkg in packages:
        icon_dir = '%s/%s/%s/icons' % (pkgdest, pkg, d.getVar('datadir'))
        if not os.path.exists(icon_dir):
            continue

        bb.note("adding hicolor-icon-theme dependency to %s" % pkg)
        rdepends = ' ' + d.getVar('MLPREFIX', False) + "hicolor-icon-theme"
        d.appendVar('RDEPENDS_%s' % pkg, rdepends)

        #gtk_icon_cache_postinst depend on gdk-pixbuf and gtk+3
        bb.note("adding gdk-pixbuf dependency to %s" % pkg)
        rdepends = ' ' + d.getVar('MLPREFIX', False) + "gdk-pixbuf"
        d.appendVar('RDEPENDS_%s' % pkg, rdepends)

        bb.note("adding gtk+3 dependency to %s" % pkg)
        rdepends = ' ' + d.getVar('MLPREFIX', False) + "gtk+3"
        d.appendVar('RDEPENDS_%s' % pkg, rdepends)

        bb.note("adding gtk-icon-cache postinst and postrm scripts to %s" % pkg)

        postinst = d.getVar('pkg_postinst_%s' % pkg)
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += d.getVar('gtk_icon_cache_postinst')
        d.setVar('pkg_postinst_%s' % pkg, postinst)

        postrm = d.getVar('pkg_postrm_%s' % pkg)
        if not postrm:
            postrm = '#!/bin/sh\n'
        postrm += d.getVar('gtk_icon_cache_postrm')
        d.setVar('pkg_postrm_%s' % pkg, postrm)
}

