SUMMARY = "Canonical source for classifiers on PyPI (pypi.org)."
HOMEPAGE = "https://github.com/pypa/trove-classifiers"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "c31a7e92f965f060a244b57d8ed5ee6f53fcb413ee17ce790e00577cb369ad99"

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
