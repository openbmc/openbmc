SUMMARY = "GTK plugin for stylus based systems"
SECTION = "libs"
DEPENDS = "gtk+"
LICENSE = "LGPL-2.0+"
LIC_FILES_CHKSUM = "file://tap.c;beginline=1;endline=20;md5=71756eeb144e9eeb177c69aa672b1635"
PR = "r4"

inherit autotools pkgconfig

SRC_URI = "http://burtonini.com/temp/${BP}.tar.gz \
    file://gtkstylus.sh"
SRC_URI[md5sum] = "fa1c82cd9fd2fafd7ff10d78eb5781c5"
SRC_URI[sha256sum] = "383e0a22a537f653b8d41688277560f95678a042967198085ec7caa1a5cc2f4c"

do_install_append() {
    install -d ${D}/${sysconfdir}/X11/Xsession.d
    install -m 755 ${WORKDIR}/gtkstylus.sh ${D}/${sysconfdir}/X11/Xsession.d/45gtkstylus
}

# Horrible but rpm falls over if you use '*'
GTKVER = "2.10.0"

FILES_${PN} = "${sysconfdir} \
               ${libdir}/gtk-2.0/${GTKVER}/modules/libgtkstylus.so.*"
FILES_${PN}-dbg += "${libdir}/gtk-2.0/${GTKVER}/modules/.debug"
FILES_${PN}-dev += "${libdir}/gtk-2.0/${GTKVER}/modules/libgtkstylus.so"
FILES_${PN}-staticdev += "${libdir}/gtk-2.0/${GTKVER}/modules/libgtkstylus.*a"
