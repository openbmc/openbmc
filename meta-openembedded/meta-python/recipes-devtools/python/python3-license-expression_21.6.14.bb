SUMMARY = "Utility library to parse, compare, simplify and normalize license expressions"
HOMEPAGE = "https://github.com/nexB/license-expression"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://apache-2.0.LICENSE;md5=9429839cdc4b292ff46e88b524c6e0c9"

SRC_URI[sha256sum] = "9de87a427c9a449eee7913472fb9ed03b63036295547369fdbf95f76a8b924b2"

inherit pypi ptest python_setuptools_build_meta

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-booleanpy \
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
