def xfce_verdir(v):
    import re
    m = re.match("^([0-9]+)\.([0-9]+)", v)
    return "%s.%s" % (m.group(1), m.group(2))

HOMEPAGE = "http://www.xfce.org"
SRC_URI = "http://archive.xfce.org/src/xfce/${BPN}/${@xfce_verdir("${PV}")}/${BPN}-${PV}.tar.bz2"

inherit autotools gettext gtk-icon-cache pkgconfig

DEPENDS += "intltool-native"

FILES_${PN} += "${datadir}/icons/* ${datadir}/applications/* ${libdir}/xfce4/modules/*.so*"
FILES_${PN}-doc += "${datadir}/xfce4/doc"

FILES_${PN}-dev += "${libdir}/xfce4/*/*.la"
FILES_${PN}-dev += "${libdir}/xfce4/*/*/*.la"
FILES_${PN}-staticdev += "${libdir}/xfce4/*/*.a"
FILES_${PN}-staticdev += "${libdir}/xfce4/*/*/*.a"
FILES_${PN}-dbg += "${libdir}/xfce4/*/.debug"
FILES_${PN}-dbg += "${libdir}/xfce4/*/*/.debug"

