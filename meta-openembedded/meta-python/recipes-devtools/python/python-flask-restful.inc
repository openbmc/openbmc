SUMMARY = "Simple framework for creating REST APIs"
DESCRIPTION = "\
Flask-RESTful is an extension for Flask that adds support for quickly building \
REST APIs"
HOMEPAGE = "https://github.com/flask-restful/flask-restful"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=685bb55ed99a366bb431995f5eef2783"

SRC_URI[md5sum] = "8243ca101185b130d8fa529cbdbdbddd"
SRC_URI[sha256sum] = "5795519501347e108c436b693ff9a4d7b373a3ac9069627d64e4001c05dd3407"

inherit pypi

PYPI_PACKAGE = "Flask-RESTful"

RDEPENDS_${PN} = "${PYTHON_PN}-flask"
