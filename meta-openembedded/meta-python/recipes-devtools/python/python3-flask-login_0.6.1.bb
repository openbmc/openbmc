SUMMARY = "User session management for Flask"
DESCRIPTION = "Flask-Login provides user session management for Flask. \
It handles the common tasks of logging in, logging out, and remembering \
your users’ sessions over extended periods of time."
HOMEPAGE = " https://github.com/maxcountryman/flask-login"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8aa87a1cd9fa41d969ad32cfdac2c596"

SRC_URI[sha256sum] = "1306d474a270a036d6fd14f45640c4d77355e4f1c67ca4331b372d3448997b8c"

PYPI_PACKAGE = "Flask-Login"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target = "${PYTHON_PN}-flask"
