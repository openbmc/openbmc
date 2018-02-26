DESCRIPTION = "Adds SQLAlchemy support to your Flask application."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5ed1b8cc741515a835a7f6bf2f62ef4a"

SRC_URI[md5sum] = "c86449f44e90e996142872ac4b97f7ee"
SRC_URI[sha256sum] = "da19ff62e31f82825b5467811c76e2a1c60bba927e175fe091a6f739063dd829"

PYPI_PACKAGE = "Flask-SQLAlchemy"

inherit pypi setuptools

RDEPENDS_${PN} = "${PYTHON_PN}-sqlalchemy ${PYTHON_PN}-flask"
