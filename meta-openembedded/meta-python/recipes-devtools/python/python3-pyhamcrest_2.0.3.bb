SUMMARY = "Hamcrest framework for matcher objects"
HOMEPAGE = "https://github.com/hamcrest/PyHamcrest"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=79391bf1501c898472d043f36e960612"

PYPI_PACKAGE = "PyHamcrest"

SRC_URI[sha256sum] = "dfb19cf6d71743e086fbb761ed7faea5aacbc8ec10c17a08b93ecde39192a3db"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "${PYTHON_PN}-six"
