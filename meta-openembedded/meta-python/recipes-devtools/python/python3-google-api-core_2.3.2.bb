DESCRIPTION = "Google API client core library"
HOMEPAGE = "https://github.com/googleapis/python-api-core"
AUTHOR = "Google LLC"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pypi setuptools3

SRC_URI[sha256sum] = "c8889f45cf58deca522888ae1d39b2a25e93e7d1b019ae8cee6456d5c726a40c"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-asyncio \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-googleapis-common-protos \
    ${PYTHON_PN}-google-auth \
    ${PYTHON_PN}-grpcio \
    ${PYTHON_PN}-protobuf \
    ${PYTHON_PN}-pytz \
    ${PYTHON_PN}-requests \
    ${PYTHON_PN}-six \
"
