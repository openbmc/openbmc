SUMMARY = "Thin-wrapper around the mock package for easier use with pytest"
HOMEPAGE = "https://github.com/pytest-dev/pytest-mock/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=b2ddb1e69238461b7e4ef2a84d874109 \
"

SRC_URI = " \
    git://github.com/pytest-dev/pytest-mock;branch=main;protocol=https \
    file://run-ptest \
    file://0001-test_pytest_mock-skip-args-introspection-tests.patch \
    file://403.patch \
"
SRCREV = "69adc6f76c1a7baf4e7a728da9eec38741d5783e"

inherit setuptools3 ptest

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-pytest \
    python3-pytest-asyncio \
    python3-unittest \
    python3-unittest-automake-output \
"

S = "${WORKDIR}/git"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests ${D}${PTEST_PATH}/
}
