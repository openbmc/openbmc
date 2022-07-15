SUMMARY = "Duktape embeddable Javascript engine"
DESCRIPTION = "Duktape is an embeddable Javascript engine, with a focus on portability and compact footprint."
HOMEPAGE = "https://duktape.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b7825df97b52f926fc71300f7880408"

SRC_URI = "https://duktape.org/duktape-${PV}.tar.xz \
           file://run-ptest \
          "
inherit ptest

SRC_URI[sha256sum] = "90f8d2fa8b5567c6899830ddef2c03f3c27960b11aca222fa17aa7ac613c2890"

EXTRA_OEMAKE = "INSTALL_PREFIX='${prefix}' DESTDIR='${D}' LIBDIR='/${baselib}'"

do_compile () {
    oe_runmake -f Makefile.sharedlibrary INSTALL_PREFIX="${prefix}" DESTDIR="${D}"
}

do_compile_ptest() {
    oe_runmake -f Makefile.hello INSTALL_PREFIX="${prefix}" DESTDIR="${D}"
    oe_runmake -f Makefile.eval INSTALL_PREFIX="${prefix}" DESTDIR="${D}"
    oe_runmake -f Makefile.eventloop INSTALL_PREFIX="${prefix}" DESTDIR="${D}"
}

do_install () {
    oe_runmake -f Makefile.sharedlibrary INSTALL_PREFIX="${prefix}" DESTDIR="${D}" install
    # libduktaped is identical to libduktape but has an hard-coded -g build flags, remove it
    rm -f ${D}${libdir}/libduktaped.so*
}

do_install_ptest() {
    install -m 0755 "${WORKDIR}/duktape-2.7.0/hello" "${D}${PTEST_PATH}"
    install -m 0755 "${WORKDIR}/duktape-2.7.0/eval" "${D}${PTEST_PATH}"
    install -m 0755 "${WORKDIR}/duktape-2.7.0/evloop" "${D}${PTEST_PATH}"
    install -m 0755 "${WORKDIR}/duktape-2.7.0/examples/eventloop/timer-test.js" "${D}${PTEST_PATH}"
    install -m 0755 "${WORKDIR}/duktape-2.7.0/examples/eventloop/ecma_eventloop.js" "${D}${PTEST_PATH}"
}

RDEPENDS:${PN}-ptest += "make"
