SUMMARY = "C#-like programming language for easing GObject programming"
DESCRIPTION = "Vala is a C#-like language dedicated to ease GObject programming. \
Vala compiles to plain C and has no runtime environment nor penalities whatsoever."
SECTION = "devel"
DEPENDS = "bison-native flex-native libxslt-native glib-2.0"
BBCLASSEXTEND = "native"
HOMEPAGE = "http://vala-project.org"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

SHRT_VER = "${@d.getVar('PV',1).split('.')[0]}.${@d.getVar('PV',1).split('.')[1]}"

SRC_URI = "http://ftp.gnome.org/pub/GNOME/sources/${BPN}/${SHRT_VER}/${BP}.tar.xz"
inherit autotools pkgconfig

FILES_${PN} += "${datadir}/${BPN}-${SHRT_VER}/vapi ${libdir}/${BPN}-${SHRT_VER}/"
FILES_${PN}-doc += "${datadir}/devhelp"
FILES_${PN}-dbg += "${libdir}/${BPN}-${SHRT_VER}/.debug"
