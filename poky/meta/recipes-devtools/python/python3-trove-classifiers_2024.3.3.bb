SUMMARY = "Canonical source for classifiers on PyPI (pypi.org)."
HOMEPAGE = "https://github.com/pypa/trove-classifiers"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "df7edff9c67ff86b733628998330b180e81d125b1e096536d83ac0fd79673fdc"

inherit pypi python_setuptools_build_meta ptest

DEPENDS += " python3-calver-native"

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
       python3-pytest \
       python3-unittest-automake-output \
"

do_install_ptest() {
      install -d ${D}${PTEST_PATH}/tests
      cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
