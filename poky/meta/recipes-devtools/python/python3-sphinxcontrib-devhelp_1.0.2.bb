DESCRIPTION = "sphinxcontrib-devhelp is a sphinx extension which outputs Devhelp document."
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd30d9972a142c857a80c9f312e92b93"

SRC_URI[sha256sum] = "ff7f1afa7b9642e7060379360a67e9c41e8f3121f2ce9164266f61b9f4b338e4"

PYPI_PACKAGE = "sphinxcontrib-devhelp"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
