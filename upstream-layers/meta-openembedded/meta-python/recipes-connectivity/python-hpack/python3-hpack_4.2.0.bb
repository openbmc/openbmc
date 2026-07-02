DESCRIPTION = "Pure-Python HPACK header compression"
HOMEPAGE = "https://github.com/python-hyper/hpack"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5bf1c68e73fbaec2b1687b7e71514393"

SRC_URI[sha256sum] = "0895cfa3b5531fc65fe439c05eb65144f123bf7a394fcaa56aa423548d8e45c0"

inherit ptest-python-pytest pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-logging"
RDEPENDS:${PN}-ptest += "python3-hypothesis"

do_install_ptest:append(){
    # One test is failing due to missing fixtures, upstream's recommended
    # solution is to delete this file (as of v4.1.0):
    # https://github.com/python-hyper/hpack/issues/272
    rm -f ${D}${PTEST_PATH}/${PTEST_PYTEST_DIR}/conftest.py
}
