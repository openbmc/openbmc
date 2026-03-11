SUMMARY = "Thin-wrapper around the mock package for easier use with pytest"
HOMEPAGE = "https://github.com/pytest-dev/pytest-mock/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=b2ddb1e69238461b7e4ef2a84d874109 \
"

SRC_URI += " \
    file://run-ptest \
"
SRC_URI[sha256sum] = "2719255a1efeceadbc056d6bf3df3d1c5015530fb40cf347c0f9afac88410bd0" 

inherit pypi python_setuptools_build_meta ptest

PYPI_PACKAGE = "pytest-mock"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN}-ptest += " \
    python3-misc \
    python3-mock \
    python3-pytest \
    python3-pytest-asyncio \
    python3-threading \
    python3-tox \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests ${D}${PTEST_PATH}/
}
