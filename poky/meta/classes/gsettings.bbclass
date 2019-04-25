# A bbclass to handle installed GSettings (glib) schemas, updated the compiled
# form on package install and remove.
#
# The compiled schemas are platform-agnostic, so we can depend on
# glib-2.0-native for the native tool and run the postinst script when the
# rootfs builds to save a little time on first boot.

# TODO use a trigger so that this runs once per package operation run

GSETTINGS_PACKAGE ?= "${PN}"

python __anonymous() {
    pkg = d.getVar("GSETTINGS_PACKAGE")
    if pkg:
        d.appendVar("PACKAGE_WRITE_DEPS", " glib-2.0-native")
        d.appendVar("RDEPENDS_" + pkg, " ${MLPREFIX}glib-2.0-utils")
        d.appendVar("FILES_" + pkg, " ${datadir}/glib-2.0/schemas")
}

gsettings_postinstrm () {
	glib-compile-schemas $D${datadir}/glib-2.0/schemas
}

python populate_packages_append () {
    pkg = d.getVar('GSETTINGS_PACKAGE')
    if pkg:
        bb.note("adding gsettings postinst scripts to %s" % pkg)

        postinst = d.getVar('pkg_postinst_%s' % pkg) or d.getVar('pkg_postinst')
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += d.getVar('gsettings_postinstrm')
        d.setVar('pkg_postinst_%s' % pkg, postinst)

        bb.note("adding gsettings postrm scripts to %s" % pkg)

        postrm = d.getVar('pkg_postrm_%s' % pkg) or d.getVar('pkg_postrm')
        if not postrm:
            postrm = '#!/bin/sh\n'
        postrm += d.getVar('gsettings_postinstrm')
        d.setVar('pkg_postrm_%s' % pkg, postrm)
}
