SUMMARY = "shared library for GIF images"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ae11c61b04b2917be39b11f78d71519a"

CVE_PRODUCT = "giflib_project:giflib"

DEPENDS = "xmlto-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/giflib/${BP}.tar.gz"
SRC_URI[sha256sum] = "31da5562f44c5f15d63340a09a4fd62b48c45620cd302f77a6d9acf0077879bd"

do_install() {
    # using autotools's default will end up in /usr/local
    oe_runmake DESTDIR=${D} PREFIX=${prefix} LIBDIR=${libdir} install
}

PACKAGES += "${PN}-utils"
FILES:${PN} = "${libdir}/libgif.so.*"
FILES:${PN}-utils = "${bindir}"

BBCLASSEXTEND = "native"

RDEPENDS:${PN}-utils = "perl"
