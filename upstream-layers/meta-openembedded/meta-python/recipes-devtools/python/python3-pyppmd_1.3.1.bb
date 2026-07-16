SUMMARY = "PPMd compression/decompression library"
HOMEPAGE = "https://pyppmd.readthedocs.io/en/latest/"
LICENSE = "LGPL-2.1-or-later"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4fbd65380cdd255951079008b364516c"

inherit pypi python_setuptools_build_meta ptest

SRC_URI[sha256sum] = "ced527f08ade4408c1bfc5264e9f97ffac8d221c9d13eca4f35ec1ec0c7b6b2e"

SRC_URI += " \
    file://0001-Ppmd7-Ppmd8-Fix-SIGABRT-in-OutputBuffer_Grow.patch \
    file://run-ptest \
"

DEPENDS += " \
    python3-setuptools-scm-native \
    python3-toml-native \
    python3-wheel-native \
"

RDEPENDS:${PN} += "\
    python3-email \
    python3-importlib-metadata \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-pytest-benchmark \
    python3-core \
    python3-crypt \
    python3-datetime \
    python3-hypothesis \
    python3-unittest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
