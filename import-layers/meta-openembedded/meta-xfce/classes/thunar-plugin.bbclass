inherit xfce

DEPENDS += "thunar"

SRC_URI = "http://archive.xfce.org/src/thunar-plugins/${BPN}/${@'${PV}'[0:3]}/${BPN}-${PV}.tar.bz2"

FILES_${PN} += "${libdir}/thunarx-2/*.so"
FILES_${PN}-dbg += "${libdir}/thunarx-2/.debug"
FILES_${PN}-dev += "${libdir}/thunarx-2/*.la"

