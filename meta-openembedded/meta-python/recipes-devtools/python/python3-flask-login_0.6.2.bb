SUMMARY = "User session management for Flask"
DESCRIPTION = "Flask-Login provides user session management for Flask. \
It handles the common tasks of logging in, logging out, and remembering \
your users’ sessions over extended periods of time."
HOMEPAGE = " https://github.com/maxcountryman/flask-login"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8aa87a1cd9fa41d969ad32cfdac2c596"

SRC_URI[sha256sum] = "c0a7baa9fdc448cdd3dd6f0939df72eec5177b2f7abe6cb82fc934d29caac9c3"

PYPI_PACKAGE = "Flask-Login"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target = "${PYTHON_PN}-flask"
