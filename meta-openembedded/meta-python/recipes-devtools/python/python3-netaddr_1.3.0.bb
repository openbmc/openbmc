SUMMARY = "A network address manipulation library for Python."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=8afa43eca873b71d5d85dd0be1f707fa"

SRC_URI[sha256sum] = "5c3c3d9895b551b763779ba7db7a03487dc1f8e3b385af819af341ae9ef6e48a"

inherit pypi python_setuptools_build_meta ptest-python-pytest

do_install_ptest:append () {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/netaddr/tests/* ${D}${PTEST_PATH}/tests/
}

do_install_ptest:append:libc-musl () {
    sed -i -e "/--automake/ s/$/ -k 'not test_strategy_ipv6'/" ${D}${PTEST_PATH}/run-ptest
}
