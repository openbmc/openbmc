DESCRIPTION = "Adds SQLAlchemy support to your Flask application."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5ed1b8cc741515a835a7f6bf2f62ef4a"

SRC_URI[md5sum] = "dc15fe08b07b434d3d2c4063b4674b72"
SRC_URI[sha256sum] = "c5244de44cc85d2267115624d83faef3f9e8f088756788694f305a5d5ad137c5"

PYPI_PACKAGE = "Flask-SQLAlchemy"

inherit pypi setuptools

RDEPENDS_${PN} = "python-sqlalchemy"
