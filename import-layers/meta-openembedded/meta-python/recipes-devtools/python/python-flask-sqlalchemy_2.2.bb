DESCRIPTION = "Adds SQLAlchemy support to your Flask application."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5ed1b8cc741515a835a7f6bf2f62ef4a"

SRC_URI[md5sum] = "a93e6af389afac6666733e369c06c798"
SRC_URI[sha256sum] = "f0d8241efba723d7b878f73550f5d3c0fbb042416123b52b36640b7491fa208b"

PYPI_PACKAGE = "Flask-SQLAlchemy"

inherit pypi setuptools

RDEPENDS_${PN} = "${PYTHON_PN}-sqlalchemy ${PYTHON_PN}-flask"
