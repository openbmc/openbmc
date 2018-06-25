DESCRIPTION = "SQLAlchemy database migrations for Flask applications using Alembic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

SRC_URI[md5sum] = "def4106c80897b7fad2afb3bc05360e4"
SRC_URI[sha256sum] = "b709ca8642559c3c5a81a33ab10839fa052177accd5ba821047a99db635255ed"

PYPI_PACKAGE = "Flask-Migrate"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-flask-sqlalchemy \
    ${PYTHON_PN}-alembic \
    ${PYTHON_PN}-flask \
    "
