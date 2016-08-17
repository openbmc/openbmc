require oprofileui.inc

DEPENDS += "gtk+ libglade libxml2 avahi-ui gconf gettext-native"

inherit distro_features_check
ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

SRCREV = "389e1875af4721d52c7e65cf9cfffb69b0ed6a59"
PV = "0.0+git${SRCPV}"

S = "${WORKDIR}/git"

SRC_URI = "git://git.yoctoproject.org/oprofileui"

EXTRA_OECONF += "--enable-client --disable-server"

PACKAGES =+ "oprofileui-viewer"

FILES_oprofileui-viewer = "${bindir}/oparchconv ${bindir}/oprofile-viewer ${datadir}/applications/ ${datadir}/oprofileui/ ${datadir}/icons"
RDEPENDS_oprofileui-viewer = "oprofile"
