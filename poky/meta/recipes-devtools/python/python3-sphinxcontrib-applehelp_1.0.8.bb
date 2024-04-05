SUMMARY = "sphinxcontrib-applehelp is a sphinx extension which outputs Apple help books"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7715857042d4c8c0105999ca0c072c5"

SRC_URI[sha256sum] = "c40a4f96f3776c4393d933412053962fac2b84f4c99a7982ba42e09576a70619"

PYPI_PACKAGE = "sphinxcontrib_applehelp"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
