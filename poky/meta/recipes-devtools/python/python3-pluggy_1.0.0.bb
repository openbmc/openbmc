SUMMARY = "Plugin and hook calling mechanisms for python"
HOMEPAGE = "https://github.com/pytest-dev/pluggy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c8206d16fd5cc02fa9b0bb98955e5c2"

SRC_URI[sha256sum] = "4224373bacce55f955a878bf9cfa763c1e360858e330072059e10bad68531159"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"
RDEPENDS:${PN} += "${PYTHON_PN}-importlib-metadata \
                   ${PYTHON_PN}-more-itertools \
"

inherit pypi ptest python_setuptools_build_meta

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/testing
	cp -rf ${S}/testing/* ${D}${PTEST_PATH}/testing/
}
