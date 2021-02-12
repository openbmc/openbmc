DESCRIPTION = "SQLAlchemy database migrations for Flask applications using Alembic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

SRC_URI[sha256sum] = "8626af845e6071ef80c70b0dc16d373f761c981f0ad61bb143a529cab649e725"

PYPI_PACKAGE = "Flask-Migrate"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-flask-sqlalchemy \
    ${PYTHON_PN}-alembic \
    ${PYTHON_PN}-flask \
    "
