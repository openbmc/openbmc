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

LIC_FILES_CHKSUM = "file://COPYING;md5=068306999fcd4c4f2ba32622dae406ac"

SRC_URI[sha256sum] = "c04e0e66ac29741faad763a56f3c50196421d4b968009fc237c53314769bf8ad"

# Releases live in per-version directories named "<pver> (Stable)" / "<pver> (Development)",
# so match only the stable ones.
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/asio/files/asio/"
UPSTREAM_CHECK_REGEX = "files/asio/(?P<pver>\d+(\.\d+)+)%20%28Stable%29"

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
