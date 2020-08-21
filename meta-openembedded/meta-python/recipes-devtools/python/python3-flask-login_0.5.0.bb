SUMMARY = "User session management for Flask"
DESCRIPTION = "Flask-Login provides user session management for Flask. \
It handles the common tasks of logging in, logging out, and remembering \
your users’ sessions over extended periods of time."
HOMEPAGE = " https://github.com/maxcountryman/flask-login"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8aa87a1cd9fa41d969ad32cfdac2c596"

SRC_URI[md5sum] = "a2d94aa6ae935345ebc68eb3cbb5fccd"
SRC_URI[sha256sum] = "6d33aef15b5bcead780acc339464aae8a6e28f13c90d8b1cf9de8b549d1c0b4b"

PYPI_PACKAGE = "Flask-Login"

inherit pypi setuptools3

RDEPENDS_${PN}_class-target = "${PYTHON_PN}-flask"
