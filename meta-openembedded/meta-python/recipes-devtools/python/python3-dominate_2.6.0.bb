SUMMARY = "Dominate is a Python library for creating and manipulating HTML documents using an elegant DOM API."
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"

SRC_URI[md5sum] = "9f714324ca99eee98bb3c3cdbe838de6"
SRC_URI[sha256sum] = "76ec2cde23700a6fc4fee098168b9dee43b99c2f1dd0ca6a711f683e8eb7e1e4"

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
