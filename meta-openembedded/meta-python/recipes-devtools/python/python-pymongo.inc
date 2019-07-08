SUMMARY = "Python driver for MongoDB <http://www.mongodb.org>"
DESCRIPTION = "\
The PyMongo distribution contains tools for interacting with MongoDB \
database from Python. The bson package is an implementation of the BSON \
format for Python. The pymongo package is a native Python driver for \
MongoDB. The gridfs package is a gridfs implementation on top of pymongo."
HOMEPAGE = "http://github.com/mongodb/mongo-python-driver"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[md5sum] = "7a1a774160cfdc5e6942b6914a912e95"
SRC_URI[sha256sum] = "8c74e2a9b594f7962c62cef7680a4cb92a96b4e6e3c2f970790da67cc0213a7e"

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
