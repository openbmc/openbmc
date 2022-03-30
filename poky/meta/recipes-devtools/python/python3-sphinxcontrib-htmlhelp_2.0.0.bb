DESCRIPTION = "sphinxcontrib-htmlhelp is a sphinx extension which renders HTML help files"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24dce5ef6a13563241c24bc366f48886"

SRC_URI[sha256sum] = "f5f8bb2d0d629f398bf47d0d69c07bc13b65f75a81ad9e2f71a63d4b7a2f6db2"

PYPI_PACKAGE = "sphinxcontrib-htmlhelp"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
