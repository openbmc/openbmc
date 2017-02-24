SUMMARY = "PyMongo support for Flask applications"
DESCRIPTION = "PyMongo support for Flask applications."
HOMEPAGE = "https://github.com/mitsuhiko/flask/"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://flask_pymongo/wrappers.py;startline=1;endline=24;md5=dff998d5a8469503612ee7757f3a2052"

SRC_URI[md5sum] = "cbf5fbcd22719d8393e481bcac36be05"
SRC_URI[sha256sum] = "75862daece1c979a9eab5f9d1e32eb781775842273b629ae5d1cb28e6953df78"

PYPI_PACKAGE = "Flask-PyMongo"

RDEPENDS_${PN} = "${PYTHON_PN}-pymongo ${PYTHON_PN}-flask"
