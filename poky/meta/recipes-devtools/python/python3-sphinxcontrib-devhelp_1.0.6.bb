SUMMARY = "sphinxcontrib-devhelp is a sphinx extension which outputs Devhelp document."
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd30d9972a142c857a80c9f312e92b93"

SRC_URI[sha256sum] = "9893fd3f90506bc4b97bdb977ceb8fbd823989f4316b28c3841ec128544372d3"

PYPI_PACKAGE = "sphinxcontrib-devhelp"

inherit pypi python_flit_core

PYPI_ARCHIVE_NAME = "sphinxcontrib_devhelp-${PV}.${PYPI_PACKAGE_EXT}"
S = "${WORKDIR}/sphinxcontrib_devhelp-${PV}"

BBCLASSEXTEND = "native nativesdk"
