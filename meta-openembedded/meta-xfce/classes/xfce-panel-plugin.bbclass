inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "libxfce4ui libxfce4util xfce4-panel"

SRC_URI = "http://archive.xfce.org/src/panel-plugins/${BPN}/${@'${PV}'[0:3]}/${BPN}-${PV}.tar.bz2"

FILES_${PN} += "${datadir}/xfce4/panel-plugins/"
FILES_${PN} += "${datadir}/xfce4/panel/plugins/"
FILES_${PN} += "${libdir}/xfce4/panel-plugins/*.so"
FILES_${PN} += "${libdir}/xfce4/panel/plugins/*.so"
FILES_${PN}-dbg += "${libexecdir}/xfce4/panel-plugins/.debug"
