SUMMARY = "pngcheck verifies the integrity of PNG, JNG and MNG files"
HOMEPAGE = "http://www.libpng.org/pub/png/apps/pngcheck.html"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://gpl/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "zlib libpng"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/png-mng/${BPN}/${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "980bd6d9a3830fdce746d7fe3c9166ee"
SRC_URI[sha256sum] = "77f0a039ac64df55fbd06af6f872fdbad4f639d009bbb5cd5cbe4db25690f35f"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile() {
    oe_runmake -f Makefile.unx INCS=-I${STAGING_DIR_HOST}${incdir} LIBS=${STAGING_DIR_HOST}${libdir}/libz.a
}

do_install() {
    install -d ${D}${bindir}
    install pngcheck ${D}${bindir}
    install png-fix-IDAT-windowsize ${D}${bindir}
    install pngsplit ${D}${bindir}
}
