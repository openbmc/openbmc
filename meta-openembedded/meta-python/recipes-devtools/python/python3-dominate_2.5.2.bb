SUMMARY = "Dominate is a Python library for creating and manipulating HTML documents using an elegant DOM API."
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"

SRC_URI[md5sum] = "4a249c71cb009815923e9a47fa03ca08"
SRC_URI[sha256sum] = "456facce7a7ccfd9363948109cf1e978d48c58e46a46b01c71b4c0adc73b1928"

inherit pypi setuptools3 ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
    "
