def xfce_verdir(v):
    import re
    m = re.match(r"^([0-9]+)\.([0-9]+)", v)
    return "%s.%s" % (m.group(1), m.group(2))

HOMEPAGE = "http://www.xfce.org"
SRC_URI = "http://archive.xfce.org/src/xfce/${BPN}/${@xfce_verdir("${PV}")}/${BPN}-${PV}.tar.bz2"

inherit autotools gettext gtk-icon-cache pkgconfig

DEPENDS += "intltool-native"

FILES:${PN} += "${datadir}/icons/* ${datadir}/applications/* ${libdir}/xfce4/modules/*.so*"
FILES:${PN}-doc += "${datadir}/xfce4/doc"
