SUMMARY = "shared library for GIF images"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ae11c61b04b2917be39b11f78d71519a"

CVE_PRODUCT = "giflib_project:giflib"

DEPENDS = "xmlto-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/giflib/${BP}.tar.gz \
           https://sourceforge.net/p/giflib/code/ci/d54b45b0240d455bbaedee4be5203d2703e59967/tree/doc/giflib-logo.gif?format=raw;subdir=${BP}/doc;name=logo;downloadfilename=giflib-logo.gif \
           file://0001-Makefile-fix-typo-in-soname-argument.patch \
"

SRC_URI[logo.sha256sum] = "1a54383986adad1521d00e003b4c482c27e8bc60690be944a1f3319c75abc2c9"
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
