HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

SECTION = "x11/fonts"
LICENSE = "MIT-X"

DEPENDS = " encodings font-alias font-util-native"
RDEPENDS_${PN} = "encodings font-util font-alias"

XORG_PN = "${BPN}"

SRC_URI = "${XORG_MIRROR}/individual/font/${XORG_PN}-${PV}.tar.bz2"
S = "${WORKDIR}/${XORG_PN}-${PV}"

inherit autotools pkgconfig features_check

# The mkfontscale-native requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OEMAKE += "FCCACHE=/bin/true UTIL_DIR=${STAGING_DIR_TARGET}\$\(MAPFILES_PATH\)"

do_install_append() {
	find ${D}${libdir}/X11/fonts -type f -name fonts.dir | xargs rm -f
	find ${D}${libdir}/X11/fonts -type f -name fonts.scale | xargs rm -f
	find ${D}${datadir}/fonts/X11 -type f -name fonts.dir | xargs rm -f
	find ${D}${datadir}/fonts/X11 -type f -name fonts.scale | xargs rm -f	
}

FILES_${PN} += " ${libdir}/X11/fonts ${datadir}"

PACKAGE_WRITE_DEPS += "mkfontdir-native mkfontscale-native"
pkg_postinst_${PN} () {
        for fontdir in `find $D/usr/lib/X11/fonts -type d`; do
                mkfontdir $fontdir
                mkfontscale $fontdir
        done
        for fontdir in `find $D/usr/share/fonts/X11 -type d`; do
                mkfontdir $fontdir
                mkfontscale $fontdir
        done
}
