SUMMARY = "Utility library to parse, compare, simplify and normalize license expressions"
HOMEPAGE = "https://github.com/nexB/license-expression"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://apache-2.0.LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "42375df653ad85e6f5b4b0385138b2dbea1f5d66360783d8625c3e4f97f11f0c"

inherit pypi ptest python_setuptools_build_meta

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-booleanpy \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    install -d ${D}${PTEST_PATH}/src
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    cp -rf ${S}/src/* ${D}${PTEST_PATH}/src/
}
