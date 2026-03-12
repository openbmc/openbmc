SUMMARY = "Serialization library for Exceptions and Tracebacks."
HOMEPAGE = "https://github.com/ionelmc/python-tblib"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=810c4c43873f8d9331fb067a6473a960"

inherit pypi python_setuptools_build_meta ptest-python-pytest

SRC_URI[sha256sum] = "e9a652692d91bf4f743d4a15bc174c0b76afc750fe8c7b6d195cc1c1d6d2ccec"

do_install_ptest:append(){
    install -m 0644 ${S}/pytest.ini ${D}${PTEST_PATH}
    # The tests are comparing exception outputs with expected ones, and it doesn't
    # expect carets in the exception.
    sed -i 's/pytest/PYTHONNODEBUGRANGES=1 pytest/' ${D}${PTEST_PATH}/run-ptest
}

RDEPENDS:${PN}-ptest += " \
    python3-misc \
    python3-pytest-benchmark \
    python3-statistics \
    python3-twisted \
"
