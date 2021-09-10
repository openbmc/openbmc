#
# This class creates mime <-> application associations based on entry 
# 'MimeType' in *.desktop files
#

DEPENDS += "desktop-file-utils"
PACKAGE_WRITE_DEPS += "desktop-file-utils-native"
DESKTOPDIR = "${datadir}/applications"

# There are recipes out there installing their .desktop files as absolute
# symlinks. For us these are dangling and cannot be introspected for "MimeType"
# easily. By addding package-names to MIME_XDG_PACKAGES, packager can force
# proper update-desktop-database handling. Note that all introspection is
# skipped for MIME_XDG_PACKAGES not empty
MIME_XDG_PACKAGES ?= ""

mime_xdg_postinst() {
if [ "x$D" != "x" ]; then
	$INTERCEPT_DIR/postinst_intercept update_desktop_database ${PKG} \
		mlprefix=${MLPREFIX} \
		desktop_dir=${DESKTOPDIR}
else
	update-desktop-database $D${DESKTOPDIR}
fi
}

mime_xdg_postrm() {
if [ "x$D" != "x" ]; then
	$INTERCEPT_DIR/postinst_intercept update_desktop_database ${PKG} \
		mlprefix=${MLPREFIX} \
		desktop_dir=${DESKTOPDIR}
else
	update-desktop-database $D${DESKTOPDIR}
fi
}

python populate_packages:append () {
    packages = d.getVar('PACKAGES').split()
    pkgdest =  d.getVar('PKGDEST')
    desktop_base = d.getVar('DESKTOPDIR')
    forced_mime_xdg_pkgs = (d.getVar('MIME_XDG_PACKAGES') or '').split()

    for pkg in packages:
        desktops_with_mime_found = pkg in forced_mime_xdg_pkgs
        if d.getVar('MIME_XDG_PACKAGES') == '':
            desktop_dir = '%s/%s%s' % (pkgdest, pkg, desktop_base)
            if os.path.exists(desktop_dir):
                for df in os.listdir(desktop_dir):
                    if df.endswith('.desktop'):
                        try:
                            with open(desktop_dir + '/'+ df, 'r') as f:
                                for line in f.read().split('\n'):
                                    if 'MimeType' in line:
                                        desktops_with_mime_found = True
                                        break;
                        except:
                            bb.warn('Could not open %s. Set MIME_XDG_PACKAGES in recipe or add mime-xdg to INSANE_SKIP.' % desktop_dir + '/'+ df)
                    if desktops_with_mime_found:
                        break
        if desktops_with_mime_found:
            bb.note("adding mime-xdg postinst and postrm scripts to %s" % pkg)
            postinst = d.getVar('pkg_postinst:%s' % pkg)
            if not postinst:
                postinst = '#!/bin/sh\n'
            postinst += d.getVar('mime_xdg_postinst')
            d.setVar('pkg_postinst:%s' % pkg, postinst)
            postrm = d.getVar('pkg_postrm:%s' % pkg)
            if not postrm:
                postrm = '#!/bin/sh\n'
            postrm += d.getVar('mime_xdg_postrm')
            d.setVar('pkg_postrm:%s' % pkg, postrm)
            bb.note("adding desktop-file-utils dependency to %s" % pkg)
            d.appendVar('RDEPENDS:' + pkg, " " + d.getVar('MLPREFIX')+"desktop-file-utils")
}
