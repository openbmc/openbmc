SUMMARY = "Library for using PKCS"
DESCRIPTION = "\
Libp11 is a library implementing a small layer on top of PKCS \
make using PKCS"
HOMEPAGE = "https://github.com/OpenSC/libp11"
BUGTRACKER = "https://github.com/OpenSC/libp11/issues"
SECTION = "Development/Libraries"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fad9b3332be894bab9bc501572864b29"
DEPENDS = "libtool openssl"

SRC_URI = "git://github.com/OpenSC/libp11.git"
SRCREV = "9ca6a71c890b5583c8af3b4900172626bca55e72"

UPSTREAM_CHECK_GITTAGREGEX = "libp11-(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-static"

do_install_append () {
    rm -rf ${D}${docdir}/${BPN}
}

FILES_${PN} += "${libdir}/engines*/pkcs11.so"
FILES_${PN}-dev += "${libdir}/engines*/libpkcs11${SOLIBSDEV}"

BBCLASSEXTEND = "native"
