SUMMARY = "A tiny C support library"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"

SRC_URI = "http://www.balabit.com/downloads/files/libol/0.3/${BP}.tar.gz \
           file://configure.patch"
SRC_URI[md5sum] = "cbadf4b7ea276dfa85acc38a1cc5ff17"
SRC_URI[sha256sum] = "9de3bf13297ff882e02a1e6e5f6bf760a544aff92a9d8a1cf4328a32005cefe7"

inherit autotools binconfig

do_compile_prepend() {
    install ${S}/utils/make_class.in ${B}/utils
}

do_install_append() {
    rm -fr ${D}${bindir}
}
