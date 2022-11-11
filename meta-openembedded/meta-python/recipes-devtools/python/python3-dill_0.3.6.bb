SUMMARY = "Serialize all of python"
HOMEPAGE = "https://pypi.org/project/dill/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61f24e44fc855bde43ed5a1524a37bc4"

SRC_URI[sha256sum] = "e5db55f3687856d8fbdab002ed78544e1c4559a130302693d839dfe8f93f2373"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "tar.gz"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-multiprocessing \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-profile \
    ${PYTHON_PN}-core \
"
