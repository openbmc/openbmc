SUMMARY = "PyMongo support for Flask applications"
DESCRIPTION = "PyMongo support for Flask applications."
HOMEPAGE = "https://github.com/mitsuhiko/flask/"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://flask_pymongo/wrappers.py;startline=1;endline=24;md5=424c4e1047d28e01b4e4634a069c019d"

SRC_URI[md5sum] = "d7a1b717f27154e1f3a355f8ec2467d6"
SRC_URI[sha256sum] = "2baaa2ba5107d72b3a8bd4b5c0c8881316e35340ad1ae979cc13f1f3c8843b3d"

PYPI_PACKAGE = "Flask-PyMongo"

RDEPENDS_${PN} = "${PYTHON_PN}-pymongo ${PYTHON_PN}-flask"
