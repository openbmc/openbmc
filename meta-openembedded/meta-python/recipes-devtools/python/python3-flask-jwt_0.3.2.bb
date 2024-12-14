DESCRIPTION = "JWT token authentication for Flask apps"
HOMEPAGE = "https://github.com/mattupstate/flask-jwt"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ff00db41c47ec84b4567a8b3c246a959"

PYPI_PACKAGE = "Flask-JWT"

SRC_URI[sha256sum] = "49c0672fbde0f1cd3374bd834918d28956e3c521c7e00089cdc5380d323bd0ad"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-pyjwt python3-flask"
