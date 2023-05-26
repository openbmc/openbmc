SUMMARY = "Simple framework for creating REST APIs"
DESCRIPTION = "\
Flask-RESTful is an extension for Flask that adds support for quickly building \
REST APIs"
HOMEPAGE = "https://github.com/flask-restful/flask-restful"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=685bb55ed99a366bb431995f5eef2783"

SRC_URI[sha256sum] = "fe4af2ef0027df8f9b4f797aba20c5566801b6ade995ac63b588abf1a59cec37"

inherit pypi setuptools3

PYPI_PACKAGE = "Flask-RESTful"

RDEPENDS:${PN} = "${PYTHON_PN}-flask"
