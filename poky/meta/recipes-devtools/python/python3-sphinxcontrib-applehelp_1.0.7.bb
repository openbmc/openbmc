SUMMARY = "sphinxcontrib-applehelp is a sphinx extension which outputs Apple help books"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7715857042d4c8c0105999ca0c072c5"

SRC_URI[sha256sum] = "39fdc8d762d33b01a7d8f026a3b7d71563ea3b72787d5f00ad8465bd9d6dfbfa"

inherit pypi python_flit_core

PYPI_ARCHIVE_NAME = "sphinxcontrib_applehelp-${PV}.${PYPI_PACKAGE_EXT}"
S = "${WORKDIR}/sphinxcontrib_applehelp-${PV}"

BBCLASSEXTEND = "native nativesdk"
