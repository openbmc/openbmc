DESCRIPTION = "SQLAlchemy database migrations for Flask applications using Alembic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

SRC_URI[md5sum] = "bedeb0366740fda6912fea683be11968"
SRC_URI[sha256sum] = "a96ff1875a49a40bd3e8ac04fce73fdb0870b9211e6168608cbafa4eb839d502"

PYPI_PACKAGE = "Flask-Migrate"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-flask-sqlalchemy \
    ${PYTHON_PN}-alembic \
    ${PYTHON_PN}-flask \
    "
