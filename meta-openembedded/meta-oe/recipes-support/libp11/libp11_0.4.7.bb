SUMMARY = "Library for using PKCS"
DESCRIPTION = "\
Libp11 is a library implementing a small layer on top of PKCS \
make using PKCS"
HOMEPAGE = "http://www.opensc-project.org/libp11"
SECTION = "Development/Libraries"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fad9b3332be894bab9bc501572864b29"
DEPENDS = "libtool openssl"

SRC_URI = "git://github.com/OpenSC/libp11.git"
SRCREV = "64569a391897bd29c5060b19fa4613e619e59277"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-static"

do_install_append () {
    rm -rf ${D}${libdir}/*.la
    rm -rf ${D}${docdir}/${BPN}
}

FILES_${PN} += "${libdir}/engines*/pkcs11.so"
FILES_${PN}-dev += "${libdir}/engines*/libpkcs11${SOLIBSDEV}"
