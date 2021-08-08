DESCRIPTION = "Implements a XML/HTML/XHTML Markup safe string for Python"
HOMEPAGE = "http://github.com/mitsuhiko/markupsafe"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[sha256sum] = "594c67807fb16238b30c44bdf74f36c02cdf22d1c8cda91ef8a0ed8dabf5620a"

PYPI_PACKAGE = "MarkupSafe"
inherit pypi setuptools3
inherit ${@bb.utils.filter('DISTRO_FEATURES', 'ptest', d)}

RDEPENDS:${PN} += "${PYTHON_PN}-stringold"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -f ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
