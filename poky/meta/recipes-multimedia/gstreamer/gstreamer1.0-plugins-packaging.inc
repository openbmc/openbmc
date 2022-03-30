# This .inc file contains functionality for automatically splitting
# built plugins into individual packages for each plugin. A -meta
# package is also set up that has no files of its own, but contains
# the names of all plugin packages in its RDEPENDS list.
#
# This is mainly used by the gstreamer1.0-plugins-* plugin set recipes,
# but can be used in any recipe that produces GStreamer plugins.

# Dynamically generate packages for all enabled plugins
PACKAGES_DYNAMIC = "^${PN}-.* ^libgst.*"

PACKAGESPLITFUNCS:prepend = " split_gstreamer10_packages "
PACKAGESPLITFUNCS:append = " set_gstreamer10_metapkg_rdepends "

python split_gstreamer10_packages () {
    gst_libdir = d.expand('${libdir}/gstreamer-1.0')
    postinst = d.getVar('plugin_postinst')
    glibdir = d.getVar('libdir')

    # GStreamer libraries
    do_split_packages(d, glibdir, r'^lib(.*)\.so\.*', 'lib%s', 'GStreamer 1.0 %s library', extra_depends='', allow_links=True)
    # GStreamer plugin shared objects
    do_split_packages(d, gst_libdir, r'libgst(.*)\.so$', d.expand('${PN}-%s'), 'GStreamer 1.0 plugin for %s', postinst=postinst, extra_depends='')
    # GObject introspection files for GStreamer plugins
    do_split_packages(d, glibdir+'/girepository-1.0', r'Gst(.*)-1.0\.typelib$', d.expand('${PN}-%s-typelib'), 'GStreamer 1.0 typelib file for %s', postinst=postinst, extra_depends='')
    # Static GStreamer libraries for development
    do_split_packages(d, gst_libdir, r'libgst(.*)\.a$', d.expand('${PN}-%s-staticdev'), 'GStreamer 1.0 plugin for %s (static development files)', extra_depends='${PN}-staticdev')
}

python set_gstreamer10_metapkg_rdepends () {
    import os
    import oe.utils

    # Go through all generated packages (excluding the main package and
    # the -meta package itself) and add them to the -meta package as RDEPENDS.

    pn = d.getVar('PN')
    metapkg =  pn + '-meta'
    d.setVar('ALLOW_EMPTY:' + metapkg, "1")
    d.setVar('FILES:' + metapkg, "")
    exclude = [ pn, pn + '-meta' ]
    metapkg_rdepends = []
    pkgdest = d.getVar('PKGDEST')
    for pkg in oe.utils.packages_filter_out_system(d):
        if pkg not in exclude and pkg not in metapkg_rdepends:
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
    d.setVar('RDEPENDS:' + metapkg, ' '.join(metapkg_rdepends))
    d.setVar('DESCRIPTION:' + metapkg, pn + ' meta package')
}

# each plugin-dev depends on PN-dev, plugin-staticdev on PN-staticdev
# so we need them even when empty (like in gst-plugins-good case)
ALLOW_EMPTY:${PN} = "1"
ALLOW_EMPTY:${PN}-dev = "1"
ALLOW_EMPTY:${PN}-staticdev = "1"

PACKAGES += "${PN}-apps ${PN}-meta ${PN}-glib"

FILES:${PN} = ""
FILES:${PN}-apps = "${bindir}"
FILES:${PN}-glib = "${datadir}/glib-2.0"

RRECOMMENDS:${PN} += "${PN}-meta"
