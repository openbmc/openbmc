PACKAGE_WRITE_DEPS += "qemu-native"
inherit qemu

GIO_MODULE_PACKAGES ??= "${PN}"

gio_module_cache_common() {
if [ "x$D" != "x" ]; then
    $INTERCEPT_DIR/postinst_intercept update_gio_module_cache ${PKG} \
            mlprefix=${MLPREFIX} \
            binprefix=${MLPREFIX} \
            libdir=${libdir} \
            libexecdir=${libexecdir} \
            base_libdir=${base_libdir} \
            bindir=${bindir}
else
    ${libexecdir}/${MLPREFIX}gio-querymodules ${libdir}/gio/modules/
fi
}

python populate_packages_append () {
    packages = d.getVar('GIO_MODULE_PACKAGES').split()

    for pkg in packages:
        bb.note("adding gio-module-cache postinst and postrm scripts to %s" % pkg)

        postinst = d.getVar('pkg_postinst_%s' % pkg)
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += d.getVar('gio_module_cache_common')
        d.setVar('pkg_postinst_%s' % pkg, postinst)

        postrm = d.getVar('pkg_postrm_%s' % pkg)
        if not postrm:
            postrm = '#!/bin/sh\n'
        postrm += d.getVar('gio_module_cache_common')
        d.setVar('pkg_postrm_%s' % pkg, postrm)
}

