# This class will update the inputmethod module cache for virtual keyboards
#
# Usage: Set GTKIMMODULES_PACKAGES to the packages that needs to update the inputmethod modules

PACKAGE_WRITE_DEPS += "qemu-native"

inherit qemu

GTKIMMODULES_PACKAGES ?= "${PN}"

gtk_immodule_cache_postinst() {
if [ "x$D" != "x" ]; then
    $INTERCEPT_DIR/postinst_intercept update_gtk_immodules_cache ${PKG} \
            mlprefix=${MLPREFIX} \
            binprefix=${MLPREFIX} \
            libdir=${libdir} \
            libexecdir=${libexecdir} \
            base_libdir=${base_libdir} \
            bindir=${bindir}
else
    if [ ! -z `which gtk-query-immodules-2.0` ]; then
        gtk-query-immodules-2.0 > ${libdir}/gtk-2.0/2.10.0/immodules.cache
    fi
    if [ ! -z `which gtk-query-immodules-3.0` ]; then
        gtk-query-immodules-3.0 > ${libdir}/gtk-3.0/3.0.0/immodules.cache
    fi
fi
}

gtk_immodule_cache_postrm() {
if [ "x$D" != "x" ]; then
    $INTERCEPT_DIR/postinst_intercept update_gtk_immodules_cache ${PKG} \
            mlprefix=${MLPREFIX} \
            binprefix=${MLPREFIX} \
            libdir=${libdir} \
            libexecdir=${libexecdir} \
            base_libdir=${base_libdir} \
            bindir=${bindir}
else
    if [ ! -z `which gtk-query-immodules-2.0` ]; then
        gtk-query-immodules-2.0 > ${libdir}/gtk-2.0/2.10.0/immodules.cache
    fi
    if [ ! -z `which gtk-query-immodules-3.0` ]; then
        gtk-query-immodules-3.0 > ${libdir}/gtk-3.0/3.0.0/immodules.cache
    fi
fi
}

python populate_packages_append () {
    gtkimmodules_pkgs = d.getVar('GTKIMMODULES_PACKAGES').split()

    for pkg in gtkimmodules_pkgs:
            bb.note("adding gtk-immodule-cache postinst and postrm scripts to %s" % pkg)

            postinst = d.getVar('pkg_postinst_%s' % pkg)
            if not postinst:
                postinst = '#!/bin/sh\n'
            postinst += d.getVar('gtk_immodule_cache_postinst')
            d.setVar('pkg_postinst_%s' % pkg, postinst)

            postrm = d.getVar('pkg_postrm_%s' % pkg)
            if not postrm:
                postrm = '#!/bin/sh\n'
            postrm += d.getVar('gtk_immodule_cache_postrm')
            d.setVar('pkg_postrm_%s' % pkg, postrm)
}

python __anonymous() {
    if not bb.data.inherits_class('native', d) and not bb.data.inherits_class('cross', d):
        gtkimmodules_check = d.getVar('GTKIMMODULES_PACKAGES', False)
        if not gtkimmodules_check:
            bb_filename = d.getVar('FILE', False)
            bb.fatal("ERROR: %s inherits gtk-immodules-cache but doesn't set GTKIMMODULES_PACKAGES" % bb_filename)
}

