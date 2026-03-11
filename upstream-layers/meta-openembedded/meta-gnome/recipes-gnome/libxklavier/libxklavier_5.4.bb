SUMMARY = "Helper lib for keyboard management"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=6e29c688d912da12b66b73e32b03d812"

DEPENDS = "xkbcomp gtk+ iso-codes libxi libxml2"

inherit autotools pkgconfig gettext gobject-introspection features_check gtk-doc

REQUIRED_DISTRO_FEATURES = "x11"

RDEPENDS:${PN} += "iso-codes xkbcomp"

SRC_URI = " \
    http://pkgs.fedoraproject.org/repo/pkgs/${BPN}/${BPN}-${PV}.tar.bz2/13af74dcb6011ecedf1e3ed122bd31fa/${BPN}-${PV}.tar.bz2 \
    file://fix-do_installe-failure.patch \
    file://0001-xklavier_config_xkb.c-Fix-keyboard-layout-settings-f.patch \
"
SRC_URI[md5sum] = "13af74dcb6011ecedf1e3ed122bd31fa"
SRC_URI[sha256sum] = "17a34194df5cbcd3b7bfd0f561d95d1f723aa1c87fca56bc2c209514460a9320"

FILES:${PN} += "${datadir}/*"

EXTRA_OECONF = "--with-xkb-bin-base=${bindir}"

do_configure:append() {
    find ${B} -name Makefile | xargs sed -i s:'-I$(includedir)':'-I.':g
    find ${B} -name Makefile | xargs sed -i s:'-I/usr/include':'-I${STAGING_INCDIR}':g
}

do_compile:append() {
    sed -i -e s:${STAGING_DIR_TARGET}::g \
           -e s:/${TARGET_SYS}::g \
              libxklavier.pc
}


