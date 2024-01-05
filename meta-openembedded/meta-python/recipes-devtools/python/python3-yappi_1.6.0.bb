SUMMARY  = "Yet Another Python Profiler"
HOMEPAGE = "https://github.com/sumerc/yappi"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71c208c9a4fd864385eb69ad4caa3bee"

SRC_URI[sha256sum] = "a9aaf72009d8c03067294151ee0470ac7a6dfa7b33baab40b198d6c1ef00430a"

SRC_URI += " \
    file://run-ptest \
    file://0001-test_functionality-convert-line-endings-to-Unix.patch \
    file://0002-Fix-import-of-tests.utils-to-enable-pytest.patch \
"

inherit pypi python_setuptools_build_meta ptest

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-pickle \
    python3-threading \
"

RDEPENDS:${PN}-ptest += " \
    python3-gevent \
    python3-multiprocessing \
    python3-pytest \
    python3-profile \
    python3-unittest-automake-output \
    python3-zopeinterface \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests ${D}${PTEST_PATH}
    cp -f ${S}/run_tests.py ${D}${PTEST_PATH}
}

