SUMMARY = "sphinxcontrib-htmlhelp is a sphinx extension which renders HTML help files"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24dce5ef6a13563241c24bc366f48886"

SRC_URI[sha256sum] = "6c26a118a05b76000738429b724a0568dbde5b72391a688577da08f11891092a"

PYPI_PACKAGE = "sphinxcontrib-htmlhelp"

inherit pypi python_flit_core

PYPI_ARCHIVE_NAME = "sphinxcontrib_htmlhelp-${PV}.${PYPI_PACKAGE_EXT}"
S = "${WORKDIR}/sphinxcontrib_htmlhelp-${PV}"

BBCLASSEXTEND = "native nativesdk"
