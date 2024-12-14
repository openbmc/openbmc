SUMMARY = "Implements a XML/HTML/XHTML Markup safe string for Python"
HOMEPAGE = "http://github.com/mitsuhiko/markupsafe"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[sha256sum] = "ee55d3edf80167e48ea11a923c7386f4669df67d7994554387f84e7d8b0a2bf0"

PYPI_PACKAGE = "markupsafe"
UPSTREAM_CHECK_PYPI_PACKAGE = "MarkupSafe"
inherit pypi python_setuptools_build_meta ptest

RDEPENDS:${PN} += "python3-html python3-stringold"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -f ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
