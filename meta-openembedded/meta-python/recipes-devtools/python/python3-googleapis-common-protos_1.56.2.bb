DESCRIPTION = "Common protobufs used in Google APIs"
HOMEPAGE = "https://github.com/googleapis/python-api-common-protos"
AUTHOR = "Google LLC"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit pypi setuptools3

SRC_URI[sha256sum] = "b09b56f5463070c2153753ef123f07d2e49235e89148e9b2459ec8ed2f68d7d3"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-grpcio \
    ${PYTHON_PN}-protobuf \
"
