DESCRIPTION = "The CAPS Audio Plugin Suite - LADSPA plugin suite"
HOMEPAGE = "http://quitte.de/dsp/caps.html"
LICENSE = "GPL-3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://quitte.de/dsp/${BPN}_${PV}.tar.bz2 \
           file://0001-basic.h-Use-c99-supported-stdint-types.patch \
           file://append_ldflags.patch \
           "

SRC_URI[md5sum] = "36b30c7c7db2d2bc5f4f54077e97b5ee"
SRC_URI[sha256sum] = "e7496c5bce05abebe3dcb635926153bbb58a9337a6e423f048d3b61d8a4f98c9"

EXTRA_OEMAKE = " \
    CC='${CXX}' \
    CFLAGS='${CFLAGS} -ffast-math -funroll-loops -fPIC -DPIC' \
    ARCH='' \
"
do_compile() {
    oe_runmake all
}

do_install() {
    install -Dm 0644 caps.so ${D}${libdir}/ladspa/caps.so
}

FILES_${PN} = "${libdir}/ladspa/"
