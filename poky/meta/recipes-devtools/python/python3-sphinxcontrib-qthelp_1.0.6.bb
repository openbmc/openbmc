SUMMARY = "Is a sphinx extension which outputs QtHelp document."
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f7a83b72ea86d04827575ec0b63430eb"

SRC_URI[sha256sum] = "62b9d1a186ab7f5ee3356d906f648cacb7a6bdb94d201ee7adf26db55092982d"

PYPI_PACKAGE = "sphinxcontrib-qthelp"

inherit pypi python_flit_core

PYPI_ARCHIVE_NAME = "sphinxcontrib_qthelp-${PV}.${PYPI_PACKAGE_EXT}"
S = "${WORKDIR}/sphinxcontrib_qthelp-${PV}"

BBCLASSEXTEND = "native nativesdk"
