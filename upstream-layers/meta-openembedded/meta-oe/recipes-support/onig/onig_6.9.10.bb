SUMMARY = "Regular expressions library"
DESCRIPTION = "Oniguruma is a modern and flexible regular expressions library. \
It encompasses features from different regular expression \
implementations that traditionally exist in different languages. \
Character encoding can be specified per regular expression object."
HOMEPAGE = "https://github.com/kkos/oniguruma"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6365c225bb5cc4321d0913f0baffa04"

SRC_URI = "\
    https://github.com/kkos/oniguruma/releases/download/v${PV}/${BP}.tar.gz \
    file://0001-build-don-t-link-against-host-system-libraries.patch \
    file://0002-build-enable-serial-tests-automake-option-for-ptest.patch \
    file://run-ptest \
"

SRC_URI[sha256sum] = "2a5cfc5ae259e4e97f86b68dfffc152cdaffe94e2060b770cb827238d769fc05"

UPSTREAM_CHECK_URI = "https://github.com/kkos/oniguruma/releases/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

BINCONFIG = "${bindir}/onig-config"

inherit autotools binconfig-disabled ptest

BBCLASSEXTEND = "native"

do_compile_ptest() {
	oe_runmake -C test buildtest-TESTS
}

do_install_ptest() {
	mkdir -p ${D}${PTEST_PATH}/tests
	install -m 0755 -t ${D}${PTEST_PATH}/tests/ ${B}/test/.libs/*
}

PROVIDES += "oniguruma"
