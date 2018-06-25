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
LIC_FILES_CHKSUM = "file://LICENSE;md5=5cc1f1e4c71f19f580458586756c02b4"

SRC_URI = "\
    https://github.com/troydhanson/${BPN}/archive/v${PV}.tar.gz;downloadfilename=${BP}.tar.gz \
    file://run-ptest \
"
UPSTREAM_CHECK_URI = "https://github.com/troydhanson/${BPN}/releases"

SRC_URI[md5sum] = "d08632a58674274c9cd87e2930f5696a"
SRC_URI[sha256sum] = "34a31d51dd7a839819cecd6f46049b4ffe031d7f3147d9a042f5504fdb1348d1"

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
RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native"
