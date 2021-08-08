SUMMARY = "Simple framework for creating REST APIs"
DESCRIPTION = "\
Flask-RESTful is an extension for Flask that adds support for quickly building \
REST APIs"
HOMEPAGE = "https://github.com/flask-restful/flask-restful"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=685bb55ed99a366bb431995f5eef2783"

SRC_URI[sha256sum] = "ccec650b835d48192138c85329ae03735e6ced58e9b2d9c2146d6c84c06fa53e"

inherit pypi setuptools3

PYPI_PACKAGE = "Flask-RESTful"

RDEPENDS:${PN} = "${PYTHON_PN}-flask"
