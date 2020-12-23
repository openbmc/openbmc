DESCRIPTION = "Adds SQLAlchemy support to your Flask application."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[md5sum] = "63a522cb82a75292dc8bc77b6d26187a"
SRC_URI[sha256sum] = "bfc7150eaf809b1c283879302f04c42791136060c6eeb12c0c6674fb1291fae5"

PYPI_PACKAGE = "Flask-SQLAlchemy"

inherit pypi setuptools3

RDEPENDS_${PN} = "${PYTHON_PN}-sqlalchemy ${PYTHON_PN}-flask"
