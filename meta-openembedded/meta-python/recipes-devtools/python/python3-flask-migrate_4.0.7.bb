DESCRIPTION = "SQLAlchemy database migrations for Flask applications using Alembic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

SRC_URI[sha256sum] = "dff7dd25113c210b069af280ea713b883f3840c1e3455274745d7355778c8622"

PYPI_PACKAGE = "Flask-Migrate"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-flask-sqlalchemy \
    python3-alembic \
    python3-flask \
    "
