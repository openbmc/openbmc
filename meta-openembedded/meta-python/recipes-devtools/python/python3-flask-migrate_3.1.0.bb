DESCRIPTION = "SQLAlchemy database migrations for Flask applications using Alembic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

SRC_URI[sha256sum] = "57d6060839e3a7f150eaab6fe4e726d9e3e7cffe2150fb223d73f92421c6d1d9"

PYPI_PACKAGE = "Flask-Migrate"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-flask-sqlalchemy \
    ${PYTHON_PN}-alembic \
    ${PYTHON_PN}-flask \
    "
