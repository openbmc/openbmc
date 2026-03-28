SUMMARY = "Asio is C++ library for network and low-level I/O programming"
DESCRIPTION = "Asio is a cross-platform C++ library for network and low-level \
        I/O programming that provides developers with a consistent asynchronous \
        model using a modern C++ approach."
HOMEPAGE = "http://think-async.com/Asio"
SECTION = "libs"
LICENSE = "BSL-1.0"

DEPENDS = "openssl"

SRC_URI = "${SOURCEFORGE_MIRROR}/asio/${BP}.tar.bz2 \
           file://0001-tests-Remove-blocking_adaptation.cpp.patch \
           file://run-ptest \
"

inherit autotools ptest

ALLOW_EMPTY:${PN} = "1"

LIC_FILES_CHKSUM = "file://COPYING;md5=92db288d8a7d89bb9c5821c447c3052c"

SRC_URI[sha256sum] = "7bf4dbe3c1ccd9cc4c94e6e6be026dcc2110f9201d286bb9500dc85d69825524"

PACKAGECONFIG ??= "boost"

PACKAGECONFIG[boost] = "--with-boost=${STAGING_LIBDIR},--without-boost,boost"

TESTDIR = "src/tests"
do_compile_ptest() {
    echo 'buildtest-TESTS: $(check_PROGRAMS)' >> ${TESTDIR}/Makefile
    oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    # copy executables
    find ${B}/${TESTDIR}/unit -type f -executable -exec cp {} ${D}${PTEST_PATH}/tests/ \;
}

BBCLASSEXTEND = "native nativesdk"
