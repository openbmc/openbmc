SUMMARY = " UTF-8 with C++ in a Portable Way"
HOMEPAGE = "https://github.com/nemtrif/utfcpp"

LICENSE = "BSL-1.0 & MIT"
LICENSE:${PN} = "BSL-1.0"
LICENSE:${PN}-ptest = "BSL-1.0 & MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=e4224ccaecb14d942c71d31bef20d78c \
                    file://tests/ftest.h;endline=25;md5=d33c6488d3b003723a5f17ac984db030"

SRC_URI = "git://github.com/nemtrif/utfcpp;protocol=https;branch=master;tag=v${PV} \
           file://run-ptest"

SRCREV = "63d64de49fd6b829f7c8694df5ab2ee625cb7134"

inherit cmake ptest

FILES:${PN}-dev += "${datadir}/utf8cpp/cmake"

EXTRA_OECMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', '-DUTF8CPP_ENABLE_TESTS=ON', '', d)}"

do_install_ptest(){
	install -d ${D}${PTEST_PATH}/tests/test_data
	install -m 0644 ${S}/tests/test_data/* ${D}${PTEST_PATH}/tests/test_data
	find ${B}/tests -type f -executable -exec install {} ${D}${PTEST_PATH}/tests/ \;
}

# the main package is a header-only library, which produces an empty package
RDEPENDS:${PN}-ptest = ""
