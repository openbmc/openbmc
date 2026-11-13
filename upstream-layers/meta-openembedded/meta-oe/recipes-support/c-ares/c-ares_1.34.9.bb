# Copyright (c) 2012-2014 LG Electronics, Inc.
SUMMARY = "c-ares is a C library that resolves names asynchronously."
HOMEPAGE = "https://c-ares.org/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=d3e72a10e08191f2ca1be3f3228d78f3"

SRC_URI = "https://github.com/c-ares/c-ares/releases/download/v${PV}/${BPN}-${PV}.tar.gz \
           file://run-ptest"
SRC_URI[sha256sum] = "9364b70a12eb59a06830fb488d9357f90100dff0d98b23ea3593a183d43e6f2f"

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

CVE_STATUS[CVE-2025-31498] = "fixed-version: Fixed since 1.34.5"
