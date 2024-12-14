DESCRIPTION="header-only library for creating parsers according to Parsing Expression Grammar"
HOMEPAGE="https://github.com/taocpp/PEGTL"
LICENSE="MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dccf35ef30bf912bb07b01d469965293"

SRC_URI = " \
	git://github.com/taocpp/PEGTL.git;protocol=https;branch=3.x \
	file://run-ptest \
"

SRCREV = "be527327653e94b02e711f7eff59285ad13e1db0"

inherit cmake ptest

S = "${WORKDIR}/git"

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/src/test/pegtl/data
    install -m 0755 ${B}/src/test/pegtl/pegtl-test-* ${D}${PTEST_PATH}/src/test/pegtl
    install ${S}/src/test/pegtl/file_*.txt ${D}${PTEST_PATH}/src/test/pegtl
    install ${S}/src/test/pegtl/data/*.json ${D}${PTEST_PATH}/src/test/pegtl/data
}

CXXFLAGS += " -Wno-error=type-limits"
