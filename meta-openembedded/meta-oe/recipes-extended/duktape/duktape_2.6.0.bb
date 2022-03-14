SUMMARY = "Duktape embeddable Javascript engine"
DESCRIPTION = "Duktape is an embeddable Javascript engine, with a focus on portability and compact footprint."
HOMEPAGE = "https://duktape.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c83446610de1f63c7ca60cfcc82dec9d"

SRC_URI = "https://duktape.org/duktape-${PV}.tar.xz \
           file://0001-Support-makefile-variables.patch \
"
SRC_URI[md5sum] = "01ee8ecf3dd5c6504543c8679661bb20"
SRC_URI[sha256sum] = "96f4a05a6c84590e53b18c59bb776aaba80a205afbbd92b82be609ba7fe75fa7"

do_compile () {
    oe_runmake -f Makefile.sharedlibrary INSTALL_PREFIX="${prefix}" DESTDIR="${D}"
}

do_install () {
    oe_runmake -f Makefile.sharedlibrary INSTALL_PREFIX="${prefix}" DESTDIR="${D}" install
    # libduktaped is identical to libduktape but has an hard-coded -g build flags, remove it
    rm -f ${D}${libdir}/libduktaped.so*
}
