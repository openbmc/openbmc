DESCRIPTION = "Adds SQLAlchemy support to your Flask application."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[sha256sum] = "2bda44b43e7cacb15d4e05ff3cc1f8bc97936cc464623424102bfc2c35e95912"

PYPI_PACKAGE = "Flask-SQLAlchemy"

inherit pypi setuptools3

RDEPENDS:${PN} = "${PYTHON_PN}-sqlalchemy ${PYTHON_PN}-flask"
