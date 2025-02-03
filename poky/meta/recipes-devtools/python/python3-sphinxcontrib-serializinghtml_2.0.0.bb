SUMMARY = 'sphinxcontrib-serializinghtml is a sphinx extension which outputs "serialized" HTML files (json and pickle).'
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENCE.rst;md5=32a84ac5cd3bbd10c4d479233ad588b6"

SRC_URI[sha256sum] = "e9d912827f872c029017a53f0ef2180b327c3f7fd23c87229f7a8e8b70031d4d"

PYPI_PACKAGE = "sphinxcontrib_serializinghtml"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
