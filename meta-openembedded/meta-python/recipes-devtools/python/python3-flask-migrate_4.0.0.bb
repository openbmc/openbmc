DESCRIPTION = "SQLAlchemy database migrations for Flask applications using Alembic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

SRC_URI[sha256sum] = "2a301c3040af6844f29d9149abe428a0f08ebc8fa149e72113bbb36fa341920a"

PYPI_PACKAGE = "Flask-Migrate"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-flask-sqlalchemy \
    ${PYTHON_PN}-alembic \
    ${PYTHON_PN}-flask \
    "
