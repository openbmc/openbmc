SUMMARY = "Command line tools and C library providing a simple interface to inotify"
HOMEPAGE = "http://wiki.github.com/rvoicilas/inotify-tools"
SECTION = "console/devel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=ac6c26e52aea428ee7f56dc2c56424c6"

SRCREV = "81c6c9881edf4844f2b8250e63f82da9cb7f5444"

SRC_URI = "git://github.com/${BPN}/${BPN};branch=master;protocol=https \
           file://run-ptest \
           "


inherit autotools ptest

EXTRA_OECONF = "--disable-doxygen"

# workaround until glibc 2.35 is fixed for this [1]
# [1] https://sourceware.org/pipermail/libc-alpha/2021-December/134215.html
CFLAGS += "-Wno-error"

PACKAGES =+ "libinotifytools"

FILES:libinotifytools = "${libdir}/lib*.so.*"

do_compile_ptest() {
        cd libinotifytools/src
        oe_runmake
        oe_runmake test
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    cp -r ${B}/libinotifytools/src/.libs/test ${D}${PTEST_PATH}/
}

FILES:${PN}-ptest += "${PTEST_PATH}"
