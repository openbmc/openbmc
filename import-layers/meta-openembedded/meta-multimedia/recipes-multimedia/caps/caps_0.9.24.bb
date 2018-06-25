DESCRIPTION = "The CAPS Audio Plugin Suite - LADSPA plugin suite"
HOMEPAGE = "http://quitte.de/dsp/caps.html"
LICENSE = "GPL-3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://quitte.de/dsp/${PN}_${PV}.tar.bz2 \
           file://Avoid-ambiguity-in-div-invocation.patch \
           file://0001-basic.h-Use-c99-supported-stdint-types.patch \
           file://append_ldflags.patch \
           file://0001-Do-not-use-obsolete-pow10f-function.patch \
           "

SRC_URI[md5sum] = "c1d634038dcb54702306c0e30cb1c626"
SRC_URI[sha256sum] = "f746feba57af316b159f0169de5d78b4fd1064c2c0c8017cb5856b2f22e83f20"

S = "${WORKDIR}/${PN}-${PV}"

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
