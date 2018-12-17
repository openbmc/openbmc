DESCRIPTION = "SQLAlchemy database migrations for Flask applications using Alembic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

SRC_URI[md5sum] = "aaeee55431d0287063657122ff41a0e6"
SRC_URI[sha256sum] = "cd1b4e6cb829eeb41c02ad9202d83bef5f4b7a036dd9fad72ce96ad1e22efb07"

PYPI_PACKAGE = "Flask-Migrate"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-flask-sqlalchemy \
    ${PYTHON_PN}-alembic \
    ${PYTHON_PN}-flask \
    "
