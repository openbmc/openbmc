SUMMARY = "pngcheck verifies the integrity of PNG, JNG and MNG files"
HOMEPAGE = "http://www.libpng.org/pub/png/apps/pngcheck.html"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://gpl/COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "zlib libpng"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/png-mng/${BPN}/${PV}/${BPN}-${PV}.tar.gz \
           file://10-pngsplit-format-strings.patch \
           file://0001-png-fix-IDAT-windowsize-Fix-format-string-errors-in-.patch \
           file://0001-make-Respect-variables-from-environement.patch \
           "

SRC_URI[md5sum] = "980bd6d9a3830fdce746d7fe3c9166ee"
SRC_URI[sha256sum] = "77f0a039ac64df55fbd06af6f872fdbad4f639d009bbb5cd5cbe4db25690f35f"

CFLAGS += "-DUSE_ZLIB"

EXTRA_OEMAKE = "-f ${S}/Makefile.unx"

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${bindir}
    install pngcheck ${D}${bindir}
    install png-fix-IDAT-windowsize ${D}${bindir}
    install pngsplit ${D}${bindir}
}
