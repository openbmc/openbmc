DESCRIPTION = "Adds SQLAlchemy support to your Flask application."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[sha256sum] = "e4b68bb881802dda1a7d878b2fc84c06d1ee57fb40b874d3dc97dabfa36b8312"

PYPI_PACKAGE = "flask_sqlalchemy"
UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/Flask-SQLAlchemy"
UPSTREAM_CHECK_REGEX = "/Flask-SQLAlchemy/(?P<pver>(\d+[\.\-_]*)+)"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "${PYTHON_PN}-sqlalchemy ${PYTHON_PN}-flask"
