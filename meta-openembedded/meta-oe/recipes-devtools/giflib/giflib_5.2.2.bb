SUMMARY = "shared library for GIF images"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ae11c61b04b2917be39b11f78d71519a"

CVE_PRODUCT = "giflib_project:giflib"

DEPENDS = "xmlto-native imagemagick-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/giflib/${BP}.tar.gz \
           file://add_suffix_to_convert_binary_used_in_Makefile.patch"

SRC_URI[sha256sum] = "be7ffbd057cadebe2aa144542fd90c6838c6a083b5e8a9048b8ee3b66b29d5fb"


do_install() {
    # using autotools's default will end up in /usr/local
    oe_runmake DESTDIR=${D} PREFIX=${prefix} LIBDIR=${libdir} install
}

PACKAGES += "${PN}-utils"
FILES:${PN} = "${libdir}/libgif.so.*"
FILES:${PN}-utils = "${bindir}"

BBCLASSEXTEND = "native"

RDEPENDS:${PN}-utils = "perl"
