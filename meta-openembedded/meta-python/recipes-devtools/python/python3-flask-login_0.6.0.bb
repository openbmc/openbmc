SUMMARY = "User session management for Flask"
DESCRIPTION = "Flask-Login provides user session management for Flask. \
It handles the common tasks of logging in, logging out, and remembering \
your users’ sessions over extended periods of time."
HOMEPAGE = " https://github.com/maxcountryman/flask-login"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8aa87a1cd9fa41d969ad32cfdac2c596"

SRC_URI[sha256sum] = "aa84fcfb4c3cf09ca58c08e816b7bce73f1349ba1cf13d00d8dffc5872d5fcf6"

PYPI_PACKAGE = "Flask-Login"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target = "${PYTHON_PN}-flask"
