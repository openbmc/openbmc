SUMMARY = "Python tools to analyze security characteristics of MS Office and OLE files"
HOMEPAGE = "https://github.com/decalage2/olefile"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=062477247e75fcb78ae3e1280be9e4e1"

SRC_URI += " \
    file://run-ptest \
    file://0001-tests-test_utils-utils.py-Adjust-source-code-dir.patch \
    file://0001-oletools-olevba.py-Fix-deprecation-warnings.patch \
"

SRC_URI[sha256sum] = "ad452099f4695ffd8855113f453348200d195ee9fa341a09e197d66ee7e0b2c3"

inherit pypi setuptools3 ptest

PYPI_PACKAGE_EXT = "zip"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-core \
    python3-olefile \
    python3-colorclass \
    python3-pyparsing \
    python3-unittest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
