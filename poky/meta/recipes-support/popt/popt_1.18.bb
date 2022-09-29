SUMMARY = "Library for parsing command line options"
DESCRIPTION = "Popt is a C library for parsing command line parameters. Popt was heavily influenced by the getopt() and getopt_long() functions, but it improves on them by allowing more powerful argument expansion. Popt can parse arbitrary argv[] style arrays and automatically set variables based on command line arguments."
HOMEPAGE = "https://www.rpm.org/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cb0613c30af2a8249b8dcc67d3edb06d"

DEPENDS = "virtual/libiconv"

SRC_URI = "\
    http://ftp.rpm.org/popt/releases/popt-1.x/${BP}.tar.gz \
    file://0001-popt-test-output-format-for-ptest.patch \
    file://run-ptest \
"
SRC_URI[sha256sum] = "5159bc03a20b28ce363aa96765f37df99ea4d8850b1ece17d1e6ad5c24fdc5d1"

inherit autotools gettext ptest

RDEPENDS:${PN}-ptest += "bash"

do_compile_ptest() {
    sed 's#lt-test1#test1#g' ${S}/tests/testit.sh > ${B}/tests/testit.sh
}

do_install_ptest() {
    install ${B}/tests/.libs/test* ${D}/${PTEST_PATH}
    install ${B}/tests/.libs/tdict ${D}/${PTEST_PATH}
    install ${B}/tests/testit.sh ${D}/${PTEST_PATH}
    install ${B}/tests/test-poptrc ${D}/${PTEST_PATH}
}

BBCLASSEXTEND = "native nativesdk"
