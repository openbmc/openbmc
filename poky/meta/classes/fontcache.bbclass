#
# This class will generate the proper postinst/postrm scriptlets for font
# packages.
#

PACKAGE_WRITE_DEPS += "qemu-native"
inherit qemu

FONT_PACKAGES ??= "${PN}"
FONT_EXTRA_RDEPENDS ?= "${MLPREFIX}fontconfig-utils"
FONTCONFIG_CACHE_DIR ?= "${localstatedir}/cache/fontconfig"
FONTCONFIG_CACHE_PARAMS ?= "-v"
# You can change this to e.g. FC_DEBUG=16 to debug fc-cache issues,
# something has to be set, because qemuwrapper is using this variable after -E
# multiple variables aren't allowed because for qemu they are separated
# by comma and in -n "$D" case they should be separated by space
FONTCONFIG_CACHE_ENV ?= "FC_DEBUG=1"
fontcache_common() {
if [ -n "$D" ] ; then
	$INTERCEPT_DIR/postinst_intercept update_font_cache ${PKG} mlprefix=${MLPREFIX} binprefix=${MLPREFIX} \
		'bindir="${bindir}"' \
		'libdir="${libdir}"' \
		'libexecdir="${libexecdir}"' \
		'base_libdir="${base_libdir}"' \
		'fontconfigcachedir="${FONTCONFIG_CACHE_DIR}"' \
		'fontconfigcacheparams="${FONTCONFIG_CACHE_PARAMS}"' \
		'fontconfigcacheenv="${FONTCONFIG_CACHE_ENV}"'
else
	${FONTCONFIG_CACHE_ENV} fc-cache ${FONTCONFIG_CACHE_PARAMS}
fi
}

python () {
    font_pkgs = d.getVar('FONT_PACKAGES').split()
    deps = d.getVar("FONT_EXTRA_RDEPENDS")

    for pkg in font_pkgs:
        if deps: d.appendVar('RDEPENDS:' + pkg, ' '+deps)
}

python add_fontcache_postinsts() {
    for pkg in d.getVar('FONT_PACKAGES').split():
        bb.note("adding fonts postinst and postrm scripts to %s" % pkg)
        postinst = d.getVar('pkg_postinst:%s' % pkg) or d.getVar('pkg_postinst')
        if not postinst:
            postinst = '#!/bin/sh\n'
        postinst += d.getVar('fontcache_common')
        d.setVar('pkg_postinst:%s' % pkg, postinst)

        postrm = d.getVar('pkg_postrm:%s' % pkg) or d.getVar('pkg_postrm')
        if not postrm:
            postrm = '#!/bin/sh\n'
        postrm += d.getVar('fontcache_common')
        d.setVar('pkg_postrm:%s' % pkg, postrm)
}

PACKAGEFUNCS =+ "add_fontcache_postinsts"
