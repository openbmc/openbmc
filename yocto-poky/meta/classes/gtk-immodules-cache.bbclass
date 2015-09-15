# This class will update the inputmethod module cache for virtual keyboards
#
# Usage: Set GTKIMMODULES_PACKAGES to the packages that needs to update the inputmethod modules

DEPENDS =+ "qemu-native"

inherit qemu

GTKIMMODULES_PACKAGES ?= "${PN}"

gtk_immodule_cache_postinst() {
if [ "x$D" != "x" ]; then
    for maj_ver in 2 3; do
        if [ -x $D${bindir}/gtk-query-immodules-$maj_ver.0 ]; then
            IMFILES=$(ls $D${libdir}/gtk-$maj_ver.0/*/immodules/*.so)
            ${@qemu_run_binary(d, '$D', '${bindir}/gtk-query-immodules-$maj_ver.0')} \
                $IMFILES > $D/etc/gtk-$maj_ver.0/gtk.immodules 2>/dev/null &&
                sed -i -e "s:$D::" $D/etc/gtk-$maj_ver.0/gtk.immodules

            [ $? -ne 0 ] && exit 1
        fi
    done

    exit 0
fi
if [ ! -z `which gtk-query-immodules-2.0` ]; then
    gtk-query-immodules-2.0 > /etc/gtk-2.0/gtk.immodules
fi
if [ ! -z `which gtk-query-immodules-3.0` ]; then
    gtk-query-immodules-3.0 > /etc/gtk-3.0/gtk.immodules
fi
}

gtk_immodule_cache_postrm() {
if [ "x$D" != "x" ]; then
    for maj_ver in 2 3; do
        if [ -x $D${bindir}/gtk-query-immodules-$maj_ver.0 ]; then
            IMFILES=$(ls $D${libdir}/gtk-$maj_ver.0/*/immodules/*.so)
            ${@qemu_run_binary(d, '$D', '${bindir}/gtk-query-immodules-$maj_ver.0')} \
                $IMFILES > $D/etc/gtk-$maj_ver.0/gtk.immodules 2>/dev/null &&
                sed -i -e "s:$D::" $D/etc/gtk-$maj_ver.0/gtk.immodules

            [ $? -ne 0 ] && exit 1
        fi
    done

    exit 0
fi
if [ ! -z `which gtk-query-immodules-2.0` ]; then
    gtk-query-immodules-2.0 > /etc/gtk-2.0/gtk.immodules
fi
if [ ! -z `which gtk-query-immodules-3.0` ]; then
    gtk-query-immodules-3.0 > /etc/gtk-3.0/gtk.immodules
fi
}

python populate_packages_append () {
    gtkimmodules_pkgs = d.getVar('GTKIMMODULES_PACKAGES', True).split()

    for pkg in gtkimmodules_pkgs:
            bb.note("adding gtk-immodule-cache postinst and postrm scripts to %s" % pkg)

            postinst = d.getVar('pkg_postinst_%s' % pkg, True)
            if not postinst:
                postinst = '#!/bin/sh\n'
            postinst += d.getVar('gtk_immodule_cache_postinst', True)
            d.setVar('pkg_postinst_%s' % pkg, postinst)

            postrm = d.getVar('pkg_postrm_%s' % pkg, True)
            if not postrm:
                postrm = '#!/bin/sh\n'
            postrm += d.getVar('gtk_immodule_cache_postrm', True)
            d.setVar('pkg_postrm_%s' % pkg, postrm)
}

python __anonymous() {
    if not bb.data.inherits_class('native', d) and not bb.data.inherits_class('cross', d):
        gtkimmodules_check = d.getVar('GTKIMMODULES_PACKAGES', False)
        if not gtkimmodules_check:
            bb_filename = d.getVar('FILE', False)
            raise bb.build.FuncFailed("ERROR: %s inherits gtk-immodules-cache but doesn't set GTKIMMODULES_PACKAGES" % bb_filename)
}

