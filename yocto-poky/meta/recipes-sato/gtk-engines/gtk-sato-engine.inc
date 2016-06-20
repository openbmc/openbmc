SUMMARY = "Sato theme engine for GTK+"
HOMEPAGE = "http://www.o-hand.com"
BUGTRACKER = "http://bugzilla.yoctoproject.org/"

LICENSE = "LGPLv2.1 & LGPLv2+"

SECTION = "x11/base"
DEPENDS = "gtk+"
RDEPENDS_gtk-theme-sato = "gtk-sato-engine"

inherit distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

PACKAGES += "gtk-theme-sato"
FILES_${PN} = "${libdir}/gtk-2.0/*/engines/*.so "
FILES_${PN}-dev = "${libdir}/gtk-2.0/*/engines/*.la"
FILES_gtk-theme-sato = "${datadir}/icons ${datadir}/themes"

inherit autotools-brokensep pkgconfig

do_configure_prepend() {
	for i in `ls gtk-common`; do
		ln -sf ../gtk-common/$i gtk2-engine/$i
	done
}
