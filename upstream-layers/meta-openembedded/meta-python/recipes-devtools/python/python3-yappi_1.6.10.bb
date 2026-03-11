SUMMARY  = "Yet Another Python Profiler"
HOMEPAGE = "https://github.com/sumerc/yappi"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71c208c9a4fd864385eb69ad4caa3bee"

SRC_URI[sha256sum] = "463b822727658937bd95a7d80ca9758605b8cd0014e004e9e520ec9cb4db0c92"

SRC_URI += " \
           file://0001-test_functionality-convert-line-endings-to-Unix.patch \
           file://0002-Fix-import-of-tests.utils-to-enable-pytest.patch \
           "

inherit pypi python_setuptools_build_meta ptest-python-pytest

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

do_install_ptest:append() {
    cp -f ${S}/run_tests.py ${D}${PTEST_PATH}
}

