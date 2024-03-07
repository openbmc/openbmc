SUMMARY = "A network address manipulation library for Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=8afa43eca873b71d5d85dd0be1f707fa"

SRC_URI[sha256sum] = "6eb8fedf0412c6d294d06885c110de945cf4d22d2b510d0404f4e06950857987"

inherit pypi python_setuptools_build_meta ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/netaddr/tests/* ${D}${PTEST_PATH}/tests/
}

