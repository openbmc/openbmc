DESCRIPTION = "JWT token authentication for Flask apps"
HOMEPAGE = "https://github.com/mattupstate/flask-jwt"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ff00db41c47ec84b4567a8b3c246a959"

PYPI_PACKAGE = "Flask-JWT"

SRC_URI[md5sum] = "878ad79a12afa70ad38a12d5ffd2dc1e"
SRC_URI[sha256sum] = "49c0672fbde0f1cd3374bd834918d28956e3c521c7e00089cdc5380d323bd0ad"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-pyjwt ${PYTHON_PN}-flask"
