SUMMARY = 'sphinxcontrib-serializinghtml is a sphinx extension which outputs "serialized" HTML files (json and pickle).'
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32a84ac5cd3bbd10c4d479233ad588b6"

SRC_URI[sha256sum] = "93f3f5dc458b91b192fe10c397e324f262cf163d79f3282c158e8436a2c4511f"

PYPI_PACKAGE = "sphinxcontrib-serializinghtml"

inherit pypi python_flit_core

PYPI_ARCHIVE_NAME = "sphinxcontrib_serializinghtml-${PV}.${PYPI_PACKAGE_EXT}"
S = "${WORKDIR}/sphinxcontrib_serializinghtml-${PV}"

BBCLASSEXTEND = "native nativesdk"
