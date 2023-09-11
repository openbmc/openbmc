SUMMARY = "Plugin and hook calling mechanisms for python"
HOMEPAGE = "https://github.com/pytest-dev/pluggy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c8206d16fd5cc02fa9b0bb98955e5c2"

SRC_URI[sha256sum] = "cf61ae8f126ac6f7c451172cf30e3e43d3ca77615509771b3a984a0730651e12"

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
	${PYTHON_PN}-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/testing
	cp -rf ${S}/testing/* ${D}${PTEST_PATH}/testing/
}

BBCLASSEXTEND = "native nativesdk"
