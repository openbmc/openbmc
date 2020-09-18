SUMMARY = "PyMongo support for Flask applications"
DESCRIPTION = "PyMongo support for Flask applications."
HOMEPAGE = "https://github.com/mitsuhiko/flask/"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://flask_pymongo/wrappers.py;beginline=1;endline=24;md5=424c4e1047d28e01b4e4634a069c019d"

SRC_URI[md5sum] = "94df71e6800b1d7915cc91a74b70f959"
SRC_URI[sha256sum] = "620eb02dc8808a5fcb90f26cab6cba9d6bf497b15032ae3ca99df80366e33314"

PYPI_PACKAGE = "Flask-PyMongo"

inherit pypi setuptools3

DEPENDS = "${PYTHON_PN}-vcversioner ${PYTHON_PN}-vcversioner-native"

RDEPENDS_${PN} = "${PYTHON_PN}-pymongo ${PYTHON_PN}-flask"
