PACKAGESPLITFUNCS_prepend = " split_gstreamer10_packages "
PACKAGESPLITFUNCS_append = " set_metapkg_rdepends "

python split_gstreamer10_packages () {
    gst_libdir = d.expand('${libdir}/gstreamer-${LIBV}')
    postinst = d.getVar('plugin_postinst', True)
    glibdir = d.getVar('libdir', True)

    do_split_packages(d, glibdir, '^lib(.*)\.so\.*', 'lib%s', 'gstreamer %s library', extra_depends='', allow_links=True)
    do_split_packages(d, gst_libdir, 'libgst(.*)\.so$', d.expand('${PN}-%s'), 'GStreamer plugin for %s', postinst=postinst, extra_depends='')
    do_split_packages(d, glibdir+'/girepository-1.0', 'Gst(.*)-1.0\.typelib$', d.expand('${PN}-%s-typelib'), 'GStreamer typelib file for %s', postinst=postinst, extra_depends='')
    do_split_packages(d, gst_libdir, 'libgst(.*)\.la$', d.expand('${PN}-%s-dev'), 'GStreamer plugin for %s (development files)', extra_depends='${PN}-dev')
    do_split_packages(d, gst_libdir, 'libgst(.*)\.a$', d.expand('${PN}-%s-staticdev'), 'GStreamer plugin for %s (static development files)', extra_depends='${PN}-staticdev')
}

python set_metapkg_rdepends () {
    import os

    pn = d.getVar('PN', True)
    metapkg =  pn + '-meta'
    d.setVar('ALLOW_EMPTY_' + metapkg, "1")
    d.setVar('FILES_' + metapkg, "")
    blacklist = [ pn, pn + '-locale', pn + '-dev', pn + '-dbg', pn + '-doc', pn + '-meta' ]
    metapkg_rdepends = []
    packages = d.getVar('PACKAGES', True).split()
    pkgdest = d.getVar('PKGDEST', True)
    for pkg in packages[1:]:
        if not pkg in blacklist and not pkg in metapkg_rdepends and not pkg.endswith('-dev') and not pkg.endswith('-dbg') and not pkg.count('locale') and not pkg.count('-staticdev'):
            # See if the package is empty by looking at the contents of its PKGDEST subdirectory. 
            # If this subdirectory is empty, then the package is.
            # Empty packages do not get added to the meta package's RDEPENDS
            pkgdir = os.path.join(pkgdest, pkg)
            if os.path.exists(pkgdir):
                dir_contents = os.listdir(pkgdir) or []
            else:
                dir_contents = []
            is_empty = len(dir_contents) == 0
            if not is_empty:
                metapkg_rdepends.append(pkg)
    d.setVar('RDEPENDS_' + metapkg, ' '.join(metapkg_rdepends))
    d.setVar('DESCRIPTION_' + metapkg, pn + ' meta package')
}

# each plugin-dev depends on PN-dev, plugin-staticdev on PN-staticdev
# so we need them even when empty (like in gst-plugins-good case)
ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}-dev = "1"
ALLOW_EMPTY_${PN}-staticdev = "1"

PACKAGES += "${PN}-apps ${PN}-meta ${PN}-glib"

FILES_${PN} = ""
FILES_${PN}-apps = "${bindir}"
FILES_${PN}-glib = "${datadir}/glib-2.0"

RRECOMMENDS_${PN} += "${PN}-meta"
