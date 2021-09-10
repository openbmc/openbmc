SUMMARY = "The Google API Client for Python is a client library for accessing the Plus, \
Moderator, and many other Google APIs."
HOMEPAGE = "https://github.com/googleapis/google-api-python-client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "a25661ec6cf4c159f41fe9c061c2bee31b2dddaf2ad787e23617048a25b53842"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-httplib2 \
    ${PYTHON_PN}-uritemplate \
    ${PYTHON_PN}-google-api-core \
"
