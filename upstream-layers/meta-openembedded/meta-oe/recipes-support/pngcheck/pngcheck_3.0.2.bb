SUMMARY = "pngcheck verifies the integrity of PNG, JNG and MNG files"
HOMEPAGE = "http://www.libpng.org/pub/png/apps/pngcheck.html"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://gpl/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "zlib libpng"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/png-mng/${BPN}/${PV}/${BPN}-${PV}.tar.gz \
           file://10-pngsplit-format-strings.patch \
           file://0001-png-fix-IDAT-windowsize-Fix-format-string-errors-in-.patch \
           file://0001-make-Respect-variables-from-environement.patch \
           "

SRC_URI[sha256sum] = "0d7e262f24116fddf2847a8ceb5c92d9f5f26efb42e9fff63ec2bb7676131ca7"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/png-mng/files/pngcheck/"
UPSTREAM_CHECK_REGEX = "${BPN}-(?P<pver>\d+(\.\d+)+)"

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
