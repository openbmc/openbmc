SUMMARY = "Python driver for MongoDB <http://www.mongodb.org>"
DESCRIPTION = "\
The PyMongo distribution contains tools for interacting with MongoDB \
database from Python. The bson package is an implementation of the BSON \
format for Python. The pymongo package is a native Python driver for \
MongoDB. The gridfs package is a gridfs implementation on top of pymongo."
HOMEPAGE = "http://github.com/mongodb/mongo-python-driver"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "fb1c56d891f9e34303c451998ef62ba52659648bb0d75b03c5e4ac223a3342c2"

inherit pypi setuptools3

PACKAGES =+ "${PYTHON_PN}-bson"

FILES:${PYTHON_PN}-bson = "${PYTHON_SITEPACKAGES_DIR}/bson/*"

RDEPENDS:${PYTHON_PN}-bson += " \
     ${PYTHON_PN}-datetime \
     ${PYTHON_PN}-json \
     ${PYTHON_PN}-netclient \
     ${PYTHON_PN}-numbers \
     ${PYTHON_PN}-threading \
"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-bson \
    ${PYTHON_PN}-pprint \
"
