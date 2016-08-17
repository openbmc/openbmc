SUMMARY = "A low level cryptographic library"
HOMEPAGE = "http://www.lysator.liu.se/~nisse/nettle/"
SECTION = "libs"

DEPENDS += "gmp"

SRC_URI = "http://www.lysator.liu.se/~nisse/archive/${BP}.tar.gz \
           file://Add-target-to-only-build-tests-not-run-them.patch \
           file://run-ptest \
           "

UPSTREAM_CHECK_REGEX = "nettle-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools ptest

EXTRA_OECONF = "--disable-openssl"

do_configure_prepend() {
       if [ ! -e ${S}/acinclude.m4 -a -e ${S}/aclocal.m4 ]; then
               cp ${S}/aclocal.m4 ${S}/acinclude.m4
       fi
}

do_compile_ptest() {
        oe_runmake buildtest
}

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/testsuite/
        install ${S}/testsuite/gold-bug.txt ${D}${PTEST_PATH}/testsuite/
        install ${S}/testsuite/*-test ${D}${PTEST_PATH}/testsuite/
        # tools can be found in PATH, not in ../tools/
        sed -i -e 's|../tools/||' ${D}${PTEST_PATH}/testsuite/*-test
        install ${B}/testsuite/*-test ${D}${PTEST_PATH}/testsuite/
}

BBCLASSEXTEND = "native nativesdk"
