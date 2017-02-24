SUMMARY = "User session management for Flask"
DESCRIPTION = "Flask-Login provides user session management for Flask. \
It handles the common tasks of logging in, logging out, and remembering \
your users’ sessions over extended periods of time."
HOMEPAGE = " https://github.com/maxcountryman/flask-login"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8aa87a1cd9fa41d969ad32cfdac2c596"

SRC_URI[md5sum] = "d95c2275d3e1c755145910077366dc45"
SRC_URI[sha256sum] = "e72eff5c35e5a31db1aeca1db5d2501be702674ea88e8f223b5d2b11644beee6"

PYPI_PACKAGE = "Flask-Login"

RDEPENDS_${PN} = "${PYTHON_PN}-flask"
