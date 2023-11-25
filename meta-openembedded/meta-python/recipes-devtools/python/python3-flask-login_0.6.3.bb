SUMMARY = "User session management for Flask"
DESCRIPTION = "Flask-Login provides user session management for Flask. \
It handles the common tasks of logging in, logging out, and remembering \
your users’ sessions over extended periods of time."
HOMEPAGE = " https://github.com/maxcountryman/flask-login"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8aa87a1cd9fa41d969ad32cfdac2c596"

SRC_URI[sha256sum] = "5e23d14a607ef12806c699590b89d0f0e0d67baeec599d75947bf9c147330333"

PYPI_PACKAGE = "Flask-Login"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target = "${PYTHON_PN}-flask"
