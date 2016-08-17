LICENSE = "LGPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

PV = "0.0.1+svnr${SRCPV}"

require e-module.inc

DEPENDS += "elementary exalt edje-native"

CFLAGS += " -I${STAGING_INCDIR}/exalt -I${STAGING_INCDIR}/exalt_dbus"

do_configure_prepend() {
    sed -i -e /po/d ${S}/configure.ac
    sed -i -e s:\ po::g ${S}/Makefile.am
}

FILES_${PN} += "${libdir}/enlightenment/modules/*/*.desktop \
                ${libdir}/enlightenment/modules/*/*.edj \
                ${libdir}/enlightenment/modules/*/*/*.so"

FILES_${PN}-staticdev += "${libdir}/enlightenment/modules/*/*/*.a"
FILES_${PN}-dev += "${libdir}/enlightenment/modules/*/*/*.la"
FILES_${PN}-dbg += "${libdir}/enlightenment/modules/*/*/.debug"
 
SRC_URI += "file://configure.patch"