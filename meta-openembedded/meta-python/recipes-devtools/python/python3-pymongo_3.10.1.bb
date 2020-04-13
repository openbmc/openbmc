SUMMARY = "Python driver for MongoDB <http://www.mongodb.org>"
DESCRIPTION = "\
The PyMongo distribution contains tools for interacting with MongoDB \
database from Python. The bson package is an implementation of the BSON \
format for Python. The pymongo package is a native Python driver for \
MongoDB. The gridfs package is a gridfs implementation on top of pymongo."
HOMEPAGE = "http://github.com/mongodb/mongo-python-driver"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[md5sum] = "e31b9c0190c9eaf1f792d0277b2a8ebe"
SRC_URI[sha256sum] = "993257f6ca3cde55332af1f62af3e04ca89ce63c08b56a387cdd46136c72f2fa"

inherit pypi setuptools3

PACKAGES =+ "${PYTHON_PN}-bson"

FILES_${PYTHON_PN}-bson = "${PYTHON_SITEPACKAGES_DIR}/bson/*"

RDEPENDS_${PYTHON_PN}-bson += " \
     ${PYTHON_PN}-datetime \
     ${PYTHON_PN}-json \
     ${PYTHON_PN}-netclient \
     ${PYTHON_PN}-numbers \
     ${PYTHON_PN}-threading \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-bson \
    ${PYTHON_PN}-pprint \
"
