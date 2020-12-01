DESCRIPTION = "Implements a XML/HTML/XHTML Markup safe string for Python"
HOMEPAGE = "http://github.com/mitsuhiko/markupsafe"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[md5sum] = "43fd756864fe42063068e092e220c57b"
SRC_URI[sha256sum] = "29872e92839765e546828bb7754a68c418d927cd064fd4708fab9fe9c8bb116b"

PYPI_PACKAGE = "MarkupSafe"
inherit pypi setuptools3
# ptest disabled in OE-Core for now due to missing dependencies

RDEPENDS_${PN} += "${PYTHON_PN}-stringold"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -f ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
