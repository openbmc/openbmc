DESCRIPTION = "sphinxcontrib-applehelp is a sphinx extension which outputs Apple help books"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7715857042d4c8c0105999ca0c072c5"

SRC_URI[sha256sum] = "83749f09f6ac843b8cb685277dbc818a8bf2d76cc19602699094fe9a74db529e"

PYPI_PACKAGE = "sphinxcontrib.applehelp"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
