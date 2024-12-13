SUMMARY = "sphinxcontrib-htmlhelp is a sphinx extension which renders HTML help files"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENCE.rst;md5=24dce5ef6a13563241c24bc366f48886"

SRC_URI[sha256sum] = "c9e2916ace8aad64cc13a0d233ee22317f2b9025b9cf3295249fa985cc7082e9"

PYPI_PACKAGE = "sphinxcontrib_htmlhelp"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
