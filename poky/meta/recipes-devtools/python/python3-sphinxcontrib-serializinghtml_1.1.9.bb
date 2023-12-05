SUMMARY = 'sphinxcontrib-serializinghtml is a sphinx extension which outputs "serialized" HTML files (json and pickle).'
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32a84ac5cd3bbd10c4d479233ad588b6"

SRC_URI[sha256sum] = "0c64ff898339e1fac29abd2bf5f11078f3ec413cfe9c046d3120d7ca65530b54"

PYPI_PACKAGE = "sphinxcontrib-serializinghtml"

inherit pypi python_flit_core

PYPI_ARCHIVE_NAME = "sphinxcontrib_serializinghtml-${PV}.${PYPI_PACKAGE_EXT}"
S = "${WORKDIR}/sphinxcontrib_serializinghtml-${PV}"

BBCLASSEXTEND = "native nativesdk"
