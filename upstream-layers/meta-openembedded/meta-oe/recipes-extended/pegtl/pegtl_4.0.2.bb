SUMMARY = "Header-only library for creating parsers according to Parsing Expression Grammar"
HOMEPAGE = "https://github.com/taocpp/PEGTL"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "git://github.com/taocpp/PEGTL.git;protocol=https;branch=4.x \
           file://0001-internal-declare-match_no_control-inline-instead-of-.patch \
           file://run-ptest \
           "

SRCREV = "b60f6110abd37e502d202d13a6c565410c552c33"

inherit cmake ptest

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/src/test/pegtl
    install -d ${D}${PTEST_PATH}/src/test/data
    install -m 0755 ${B}/src/test/pegtl-test-* ${D}${PTEST_PATH}/src/test/pegtl
    install -m 0644 ${S}/src/test/file_*.txt ${D}${PTEST_PATH}/src/test/
    install -m 0644 ${S}/src/test/data/* ${D}${PTEST_PATH}/src/test/data/
}
