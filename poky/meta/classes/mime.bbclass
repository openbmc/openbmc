DEPENDS += "shared-mime-info"
PACKAGE_WRITE_DEPS += "shared-mime-info-native"

mime_postinst() {
if [ "$1" = configure ]; then
	UPDATEMIMEDB=`which update-mime-database`
	if [ -x "$UPDATEMIMEDB" ] ; then
		echo "Updating MIME database... this may take a while."
		$UPDATEMIMEDB $D${datadir}/mime
	else
		echo "Missing update-mime-database, update of mime database failed!"
		exit 1
	fi
fi
}

mime_postrm() {
if [ "$1" = remove ] || [ "$1" = upgrade ]; then
	UPDATEMIMEDB=`which update-mime-database`
	if [ -x "$UPDATEMIMEDB" ] ; then
		echo "Updating MIME database... this may take a while."
		$UPDATEMIMEDB $D${datadir}/mime
	else
		echo "Missing update-mime-database, update of mime database failed!"
		exit 1
	fi
fi
}

python populate_packages_append () {
    import re
    packages = d.getVar('PACKAGES').split()
    pkgdest =  d.getVar('PKGDEST')

    for pkg in packages:
        mime_dir = '%s/%s/usr/share/mime/packages' % (pkgdest, pkg)
        mimes = []
        mime_re = re.compile(".*\.xml$")
        if os.path.exists(mime_dir):
            for f in os.listdir(mime_dir):
                if mime_re.match(f):
                    mimes.append(f)
        if mimes:
            bb.note("adding mime postinst and postrm scripts to %s" % pkg)
            postinst = d.getVar('pkg_postinst_%s' % pkg)
            if not postinst:
                postinst = '#!/bin/sh\n'
            postinst += d.getVar('mime_postinst')
            d.setVar('pkg_postinst_%s' % pkg, postinst)
            postrm = d.getVar('pkg_postrm_%s' % pkg)
            if not postrm:
                postrm = '#!/bin/sh\n'
            postrm += d.getVar('mime_postrm')
            d.setVar('pkg_postrm_%s' % pkg, postrm)
            bb.note("adding shared-mime-info-data dependency to %s" % pkg)
            d.appendVar('RDEPENDS_' + pkg, " shared-mime-info-data")
}
