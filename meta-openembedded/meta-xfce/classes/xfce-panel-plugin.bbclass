inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "libxfce4ui libxfce4util xfce4-panel"

SRC_URI = "http://archive.xfce.org/src/panel-plugins/${BPN}/${@xfce_verdir("${PV}")}/${BPN}-${PV}.tar.bz2"

FILES_${PN} += "${datadir}/xfce4/panel-plugins/"
FILES_${PN} += "${datadir}/xfce4/panel/plugins/"
FILES_${PN} += "${libdir}/xfce4/panel-plugins/*.so"
FILES_${PN} += "${libdir}/xfce4/panel/plugins/*.so"

FILES_${PN}-dev += "${libdir}/xfce4/panel/plugins/*.la"
