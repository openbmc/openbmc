SUMMARY = "sphinxcontrib-devhelp is a sphinx extension which outputs Devhelp document."
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENCE.rst;md5=fd30d9972a142c857a80c9f312e92b93"

SRC_URI[sha256sum] = "411f5d96d445d1d73bb5d52133377b4248ec79db5c793ce7dbe59e074b4dd1ad"

PYPI_PACKAGE = "sphinxcontrib_devhelp"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
