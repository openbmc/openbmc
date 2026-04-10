SUMMARY = "shared library for GIF images"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b427970b2f3a9142a4e432c78c4680f4"

CVE_PRODUCT = "giflib_project:giflib"

DEPENDS = "xmlto-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/giflib/${BP}.tar.gz \
           https://sourceforge.net/p/giflib/code/ci/d54b45b0240d455bbaedee4be5203d2703e59967/tree/doc/giflib-logo.gif?format=raw;subdir=${BP}/doc;name=logo;downloadfilename=giflib-logo.gif \
"

SRC_URI[logo.sha256sum] = "1a54383986adad1521d00e003b4c482c27e8bc60690be944a1f3319c75abc2c9"
SRC_URI[sha256sum] = "2421abb54f5906b14965d28a278fb49e1ec9fe5ebbc56244dd012383a973d5c0"

do_install() {
    # using autotools's default will end up in /usr/local
    oe_runmake DESTDIR=${D} PREFIX=${prefix} LIBDIR=${libdir} install
}

PACKAGES += "${PN}-utils"
FILES:${PN} = "${libdir}/libgif.so.*"
FILES:${PN}-utils = "${bindir}"

BBCLASSEXTEND = "native"

RDEPENDS:${PN}-utils = "perl"

CVE_STATUS[CVE-2026-23868] = "fixed-version: fixed since v6.1.2"
