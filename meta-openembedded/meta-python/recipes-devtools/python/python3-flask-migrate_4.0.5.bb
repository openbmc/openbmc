DESCRIPTION = "SQLAlchemy database migrations for Flask applications using Alembic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

SRC_URI[sha256sum] = "d3f437a8b5f3849d1bb1b60e1b818efc564c66e3fefe90b62e5db08db295e1b1"

PYPI_PACKAGE = "Flask-Migrate"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-flask-sqlalchemy \
    ${PYTHON_PN}-alembic \
    ${PYTHON_PN}-flask \
    "
