SUMMARY = "A wrapper around the stdlib `tokenize` which roundtrips."
HOMEPAGE = "https://github.com/asottile/tokenize-rt"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5fd324061c581b8d45b8cb18c560a543"

SRCREV = "1582bcf0259d183259da1761444c6fc73fa7ad9a"
PYPI_SRC_URI = "git://github.com/asottile/tokenize-rt.git;protocol=https;branch=main;tag=v${PV};destsuffix=tokenize_rt-${PV}"

inherit pypi setuptools3 ptest-python-pytest

RDEPENDS:${PN} += "python3-core"

PYPI_PACKAGE = "tokenize_rt"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

do_install_ptest:append() {
    install -d ${D}${PTEST_PATH}/testing/resources
    cp -rf ${S}/testing/resources/* ${D}${PTEST_PATH}/testing/resources/
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
