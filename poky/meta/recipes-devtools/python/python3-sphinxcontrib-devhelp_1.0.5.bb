SUMMARY = "sphinxcontrib-devhelp is a sphinx extension which outputs Devhelp document."
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd30d9972a142c857a80c9f312e92b93"

SRC_URI[sha256sum] = "63b41e0d38207ca40ebbeabcf4d8e51f76c03e78cd61abe118cf4435c73d4212"

PYPI_PACKAGE = "sphinxcontrib-devhelp"

inherit pypi python_flit_core

PYPI_ARCHIVE_NAME = "sphinxcontrib_devhelp-${PV}.${PYPI_PACKAGE_EXT}"
S = "${WORKDIR}/sphinxcontrib_devhelp-${PV}"

BBCLASSEXTEND = "native nativesdk"
