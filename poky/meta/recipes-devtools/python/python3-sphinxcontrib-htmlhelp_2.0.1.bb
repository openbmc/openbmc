DESCRIPTION = "sphinxcontrib-htmlhelp is a sphinx extension which renders HTML help files"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=24dce5ef6a13563241c24bc366f48886"

SRC_URI[sha256sum] = "0cbdd302815330058422b98a113195c9249825d681e18f11e8b1f78a2f11efff"

PYPI_PACKAGE = "sphinxcontrib-htmlhelp"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"
