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
"

SRCREV = "69adc6f76c1a7baf4e7a728da9eec38741d5783e"

SRC_URI[sha256sum] = "fbbdb085ef7c252a326fd8cdcac0aa3b1333d8811f131bdcc701002e1be7ed4f"

inherit setuptools3 ptest

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-asyncio \
    python3-pytest \
    python3-pytest-asyncio \
    python3-unittest \
"

S = "${WORKDIR}/git"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests ${D}${PTEST_PATH}/
}
