SUMMARY = "et_xmlfile is a low memory library for creating large XML files"
DESCRIPTION = "It is based upon the xmlfile module from lxml with the aim of allowing code \
to be developed that will work with both libraries. It was developed initially for \
the openpyxl project but is now a standalone module."

HOMEPAGE = "https://bitbucket.org/openpyxl/et_xmlfile/src/default/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=b3d89cae66f26c3a0799be8a96f3178b"

SRC_URI[md5sum] = "f47940fd9d556375420b2e276476cfaf"
SRC_URI[sha256sum] = "614d9722d572f6246302c4491846d2c393c199cfa4edc9af593437691683335b"

RDEPENDS_${PN} += "${PYTHON_PN}-compression ${PYTHON_PN}-io ${PYTHON_PN}-pprint ${PYTHON_PN}-shell"

inherit setuptools3
PYPI_PACKAGE ?= "et_xmlfile"
PYPI_SRC_URI ?= "https://files.pythonhosted.org/packages/source/e/et_xmlfile/et_xmlfile-1.0.1.tar.gz"
SECTION = "devel/python"
SRC_URI += "${PYPI_SRC_URI}"
S = "${WORKDIR}/${PYPI_PACKAGE}-${PV}"
