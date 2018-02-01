SUMMARY = "User session management for Flask"
DESCRIPTION = "Flask-Login provides user session management for Flask. \
It handles the common tasks of logging in, logging out, and remembering \
your users’ sessions over extended periods of time."
HOMEPAGE = " https://github.com/maxcountryman/flask-login"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8aa87a1cd9fa41d969ad32cfdac2c596"

SRC_URI[md5sum] = "e050efaa2c4dbc2d97f480a70f4d44aa"
SRC_URI[sha256sum] = "d25e356b14a59f52da0ab30c31c2ad285fa23a840f0f6971df7ed247c77082a7"

PYPI_PACKAGE = "Flask-Login"

RDEPENDS_${PN}_class-target = "${PYTHON_PN}-flask"
