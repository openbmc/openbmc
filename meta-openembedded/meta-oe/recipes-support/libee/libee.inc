SUMMARY = "An Event Expression Library inspired by CEE"
DESCRIPTION = "The core idea of libee is to provide a small \
but hopefully convenient API layer above the CEE standard."

HOMEPAGE = "http://www.libee.org"
SECTION = "base"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=1c2687de35e36ba3b999136236a66cd6"

SRC_URI = "http://www.libee.org/download/files/download/${BPN}-${PV}.tar.gz\
           file://libee-parallel-make.patch \
           file://run-ptest \
           file://ezapi1.sh \
"

inherit autotools ptest pkgconfig

DEPENDS += "libestr"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'testbench', '', d)}"

PACKAGECONFIG[debug] = "--enable-debug,--disable-debug,,"
PACKAGECONFIG[testbench] = "--enable-testbench,--disable-testbench,,"

TESTDIR = "tests"

# We presume the tests we are going to install so just build them directly.
#
do_compile_ptest() {
    oe_runmake -C ${TESTDIR} genfile ezapi1
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    install -m 0755 ${B}/${TESTDIR}/genfile ${D}${PTEST_PATH}
    install -m 0755 ${B}/${TESTDIR}/.libs/ezapi1 ${D}${PTEST_PATH}
    install -m 0755 ${UNPACKDIR}/ezapi1.sh ${D}${PTEST_PATH}
}
