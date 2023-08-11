SUMMARY = "LibTomMath is a number theoretic multiple-precision integer library"
HOMEPAGE = "https://www.libtom.net/LibTomMath"
SECTION = "libs"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=23e7e0a32e53a2b1d35f5fd9ef053402"

DEPENDS = "libtool-cross"

SRC_URI = "git://github.com/libtom/libtommath.git;protocol=https;branch=master"

SRCREV = "6ca6898bf37f583c4cc9943441cd60dd69f4b8f2"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "'PREFIX=${prefix}' 'DESTDIR=${D}' 'LIBPATH=${libdir}' 'CFLAGS=${CFLAGS}'"

do_compile() {
    oe_runmake -f makefile.shared
}

do_install() {
    oe_runmake -f makefile.shared install
}
