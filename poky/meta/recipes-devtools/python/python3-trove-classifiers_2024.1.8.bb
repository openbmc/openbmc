SUMMARY = "Canonical source for classifiers on PyPI (pypi.org)."
HOMEPAGE = "https://github.com/pypa/trove-classifiers"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "6e36caf430ff6485c4b57a4c6b364a13f6a898d16b9417c6c37467e59c14b05a"

inherit pypi python_setuptools_build_meta ptest

DEPENDS += " python3-calver-native"

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
       ${PYTHON_PN}-pytest \
       ${PYTHON_PN}-unittest-automake-output \
"

do_install_ptest() {
      install -d ${D}${PTEST_PATH}/tests
      cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
