#
# This class will generate the proper postinst/postrm scriptlets for font
# packages.
#

DEPENDS += "qemu-native"
inherit qemu

FONT_PACKAGES ??= "${PN}"
FONT_EXTRA_RDEPENDS ?= "fontconfig-utils"
FONTCONFIG_CACHE_DIR ?= "${localstatedir}/cache/fontconfig"
fontcache_common() {
if [ "x$D" != "x" ] ; then
	$INTERCEPT_DIR/postinst_intercept update_font_cache ${PKG} mlprefix=${MLPREFIX} bindir=${bindir} \
		libdir=${libdir} base_libdir=${base_libdir} fontconfigcachedir=${FONTCONFIG_CACHE_DIR}
else
	fc-cache
fi
}

python () {
    font_pkgs = d.getVar('FONT_PACKAGES', True).split()
    deps = d.getVar("FONT_EXTRA_RDEPENDS", True)

    for pkg in font_pkgs:
        if deps: d.appendVar('RDEPENDS_' + pkg, ' '+deps)
}

python add_fontcache_postinsts() {
    for pkg in d.getVar('FONT_PACKAGES', True).split():
        bb.note("adding fonts postinst and postrm scripts to %s" % pkg)
        postinst = d.getVar('pkg_postinst_%s' % pkg, True) or d.getVar('pkg_postinst', True)
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += d.getVar('fontcache_common', True)
        d.setVar('pkg_postinst_%s' % pkg, postinst)

        postrm = d.getVar('pkg_postrm_%s' % pkg, True) or d.getVar('pkg_postrm', True)
        if not postrm:
            postrm = '#!/bin/sh\n'
        postrm += d.getVar('fontcache_common', True)
        d.setVar('pkg_postrm_%s' % pkg, postrm)
}

PACKAGEFUNCS =+ "add_fontcache_postinsts"
