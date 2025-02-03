SUMMARY = "sphinxcontrib-applehelp is a sphinx extension which outputs Apple help books"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENCE.rst;md5=c7715857042d4c8c0105999ca0c072c5"

SRC_URI[sha256sum] = "2f29ef331735ce958efa4734873f084941970894c6090408b079c61b2e1c06d1"

PYPI_PACKAGE = "sphinxcontrib_applehelp"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
