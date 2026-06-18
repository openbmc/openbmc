DESCRIPTION = "header-only library for creating parsers according to Parsing Expression Grammar"
HOMEPAGE = "https://github.com/taocpp/PEGTL"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = " \
	git://github.com/taocpp/PEGTL.git;protocol=https;branch=main \
	file://run-ptest \
"

SRCREV = "708de300144de0ec8bd58491facfc5f34d0d0559"

inherit cmake ptest


do_install_ptest () {
    install -d ${D}${PTEST_PATH}/src/test/pegtl/data
    install -m 0755 ${B}/src/test/pegtl-test-* ${D}${PTEST_PATH}/src/test/pegtl
    install ${S}/src/test/file_*.txt ${D}${PTEST_PATH}/src/test/pegtl
    install ${S}/src/test/data/*.json ${D}${PTEST_PATH}/src/test/pegtl/data
}
