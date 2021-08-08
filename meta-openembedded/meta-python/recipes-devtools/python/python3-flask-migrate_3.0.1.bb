DESCRIPTION = "SQLAlchemy database migrations for Flask applications using Alembic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

SRC_URI[sha256sum] = "4d42e8f861d78cb6e9319afcba5bf76062e5efd7784184dd2a1cccd9de34a702"

PYPI_PACKAGE = "Flask-Migrate"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-flask-sqlalchemy \
    ${PYTHON_PN}-alembic \
    ${PYTHON_PN}-flask \
    "
