DESCRIPTION = "SQLAlchemy database migrations for Flask applications using Alembic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b69377f79f3f48c661701236d5a6a85"

SRC_URI[sha256sum] = "1a336b06eb2c3ace005f5f2ded8641d534c18798d64061f6ff11f79e1434126d"

PYPI_PACKAGE = "flask_migrate"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-flask-sqlalchemy \
    python3-alembic \
    python3-flask \
    "
