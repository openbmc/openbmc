SUMMARY = "PyMongo support for Flask applications"
DESCRIPTION = "PyMongo support for Flask applications."
HOMEPAGE = "https://github.com/mitsuhiko/flask/"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://flask_pymongo/wrappers.py;startline=1;endline=24;md5=dff998d5a8469503612ee7757f3a2052"

SRC_URI[md5sum] = "457a210f35ba49d2623dbcf27fbb89e1"
SRC_URI[sha256sum] = "707ddff92f5b7bdc34d401f1f3857a3d1a9f5dba76e6a1422ccbc3651e2ca521"

PYPI_PACKAGE = "Flask-PyMongo"

RDEPENDS_${PN} = "${PYTHON_PN}-pymongo ${PYTHON_PN}-flask"
