SUMMARY = "Simple framework for creating REST APIs"
DESCRIPTION = "\
Flask-RESTful is an extension for Flask that adds support for quickly building \
REST APIs"
HOMEPAGE = "https://github.com/flask-restful/flask-restful"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=685bb55ed99a366bb431995f5eef2783"

SRC_URI[md5sum] = "e8051ff104ab4b3b867ba18d28953fae"
SRC_URI[sha256sum] = "5ea9a5991abf2cb69b4aac19793faac6c032300505b325687d7c305ffaa76915"

inherit pypi setuptools3

PYPI_PACKAGE = "Flask-RESTful"

RDEPENDS_${PN} = "${PYTHON_PN}-flask"
