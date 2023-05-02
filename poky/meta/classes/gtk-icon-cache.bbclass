FILES:${PN} += "${datadir}/icons/hicolor"

GTKIC_VERSION ??= '3'

GTKPN = "${@ 'gtk4' if d.getVar('GTKIC_VERSION') == '4' else 'gtk+3' }"
GTKIC_CMD = "${@ 'gtk4-update-icon-cache' if d.getVar('GTKIC_VERSION') == '4' else 'gtk-update-icon-cache-3.0' }"

#gtk+3/gtk4 require GTK3DISTROFEATURES, DEPENDS on it make all the
#recipes inherit this class require GTK3DISTROFEATURES
inherit features_check
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS +=" ${@ '' if d.getVar('BPN') == 'hicolor-icon-theme' else 'hicolor-icon-theme' } \
            ${@ '' if d.getVar('BPN') == 'gdk-pixbuf' else 'gdk-pixbuf' } \
            ${@ '' if d.getVar('BPN') == d.getVar('GTKPN') else d.getVar('GTKPN') } \
            ${GTKPN}-native \
"

PACKAGE_WRITE_DEPS += "${GTKPN}-native gdk-pixbuf-native"

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
			${GTKIC_CMD} -fqt  $icondir
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
			${GTKIC_CMD} -qt  $icondir
		fi
	done
fi
}

python populate_packages:append () {
    packages = d.getVar('PACKAGES').split()
    pkgdest =  d.getVar('PKGDEST')
    
    for pkg in packages:
        icon_dir = '%s/%s/%s/icons' % (pkgdest, pkg, d.getVar('datadir'))
        if not os.path.exists(icon_dir):
            continue

        bb.note("adding hicolor-icon-theme dependency to %s" % pkg)
        rdepends = ' ' + d.getVar('MLPREFIX', False) + "hicolor-icon-theme"
        d.appendVar('RDEPENDS:%s' % pkg, rdepends)

        #gtk_icon_cache_postinst depend on gdk-pixbuf and gtk+3/gtk4
        bb.note("adding gdk-pixbuf dependency to %s" % pkg)
        rdepends = ' ' + d.getVar('MLPREFIX', False) + "gdk-pixbuf"
        d.appendVar('RDEPENDS:%s' % pkg, rdepends)

        bb.note("adding %s dependency to %s" % (d.getVar('GTKPN'), pkg))
        rdepends = ' ' + d.getVar('MLPREFIX', False) + d.getVar('GTKPN')
        d.appendVar('RDEPENDS:%s' % pkg, rdepends)

        bb.note("adding gtk-icon-cache postinst and postrm scripts to %s" % pkg)

        postinst = d.getVar('pkg_postinst:%s' % pkg)
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += d.getVar('gtk_icon_cache_postinst')
        d.setVar('pkg_postinst:%s' % pkg, postinst)

        postrm = d.getVar('pkg_postrm:%s' % pkg)
        if not postrm:
            postrm = '#!/bin/sh\n'
        postrm += d.getVar('gtk_icon_cache_postrm')
        d.setVar('pkg_postrm:%s' % pkg, postrm)
}

