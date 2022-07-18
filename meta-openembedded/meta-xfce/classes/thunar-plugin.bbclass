inherit xfce features_check

# xfce4 depends on libwnck3, gtk+3 and libepoxy need to be built with x11 PACKAGECONFIG.
# cairo would at least needed to be built with xlib.
ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

DEPENDS += "thunar"

SRC_URI = "http://archive.xfce.org/src/thunar-plugins/${BPN}/${@'${PV}'[0:3]}/${BPN}-${PV}.tar.bz2"

FILES:${PN} += "${libdir}/thunarx-3/*.so"

FILES:${PN}-dev += "${libdir}/thunarx-3/*.la"
