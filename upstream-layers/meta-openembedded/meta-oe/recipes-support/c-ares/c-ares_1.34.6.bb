# Copyright (c) 2012-2014 LG Electronics, Inc.
SUMMARY = "c-ares is a C library that resolves names asynchronously."
HOMEPAGE = "https://c-ares.org/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=d3e72a10e08191f2ca1be3f3228d78f3"

SRC_URI = "https://github.com/c-ares/c-ares/releases/download/v${PV}/${BPN}-${PV}.tar.gz \
           file://run-ptest"
SRC_URI[sha256sum] = "912dd7cc3b3e8a79c52fd7fb9c0f4ecf0aaa73e45efda880266a2d6e26b84ef5"

PACKAGECONFIG ?= "${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG[manpages] = ""
PACKAGECONFIG[tests] = "-DCARES_BUILD_TESTS=ON,-DCARES_BUILD_TESTS=OFF,googletest"

inherit cmake manpages pkgconfig ptest github-releases

EXTRA_OECMAKE = "-DCARES_STATIC=${@ 'ON' if d.getVar('DISABLE_STATIC') == '' else 'OFF' }"

do_install_ptest () {
	install -d ${D}${PTEST_PATH}
	install -m 0755 ${B}/bin/arestest ${D}${PTEST_PATH}
	install -m 0755 ${UNPACKDIR}/run-ptest ${D}${PTEST_PATH}
}

PACKAGE_BEFORE_PN = "${PN}-utils"

FILES:${PN}-utils = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
