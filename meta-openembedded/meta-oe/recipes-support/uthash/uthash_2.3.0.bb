SUMMARY = "Hash table and linked list for C structures"
DESCRIPTION = " uthash-dev provides a hash table implementation using C preprocessor macros.\n\
 This package also includes:\n\
  * utlist.h provides linked list macros for C structures\n\
  * utarray.h implements dynamic arrays using macros\n\
  * utstring.h implements a basic dynamic string\n\
"
HOMEPAGE = "https://troydhanson.github.io/uthash/"
SECTION = "base"
LICENSE = "BSD-1-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=736712b5904dd42f8678df7172bc5f2b"
SRCREV = "e493aa90a2833b4655927598f169c31cfcdf7861"

SRC_URI = "\
    git://github.com/troydhanson/${BPN}.git;branch=master;protocol=https \
    file://run-ptest \
"

S = "${WORKDIR}/git"

inherit ptest

do_compile[noexec] = "1"

do_compile_ptest() {
    oe_runmake -C tests tests_only TEST_TARGET=
}

do_install () {
    install -dm755 ${D}${includedir}
    install -m0644 src/*.h ${D}${includedir}
}

do_install_ptest() {
    install -dm755 ${D}${PTEST_PATH}/tests
    install -m0755 tests/test*[0-9] ${D}${PTEST_PATH}/tests
    install -m0644 tests/test*[0-9].ans ${D}${PTEST_PATH}/tests
    install -m0644 tests/test*[0-9].dat ${D}${PTEST_PATH}/tests
}

# The main package is empty and non-existent, so -dev
# should not depend on it...
RDEPENDS:${PN}-dev = ""
RDEPENDS:${PN}-ptest:remove = "${PN}"

BBCLASSEXTEND = "native"
