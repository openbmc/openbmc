DESCRIPTION = "Adds SQLAlchemy support to your Flask application."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5ed1b8cc741515a835a7f6bf2f62ef4a"

SRC_URI[md5sum] = "373ce61dfd03b593ad2eaba68c9fee35"
SRC_URI[sha256sum] = "5971b9852b5888655f11db634e87725a9031e170f37c0ce7851cf83497f56e53"

PYPI_PACKAGE = "Flask-SQLAlchemy"

RDEPENDS_${PN} = "${PYTHON_PN}-sqlalchemy ${PYTHON_PN}-flask"
