SUMMARY = "User session management for Flask"
DESCRIPTION = "Flask-Login provides user session management for Flask. \
It handles the common tasks of logging in, logging out, and remembering \
your users’ sessions over extended periods of time."
HOMEPAGE = " https://github.com/maxcountryman/flask-login"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8aa87a1cd9fa41d969ad32cfdac2c596"

SRC_URI[md5sum] = "c0a7eaf28623f0aeac4929dc05a7a064"
SRC_URI[sha256sum] = "83d5f10e5c4f214feed6cc41c212db63a58a15ac32e56df81591bfa0a5cee3e5"

PYPI_PACKAGE = "Flask-Login"

RDEPENDS_${PN} = "${PYTHON_PN}-flask"
