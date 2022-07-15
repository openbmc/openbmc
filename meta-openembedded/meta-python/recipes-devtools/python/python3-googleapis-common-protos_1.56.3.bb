DESCRIPTION = "Common protobufs used in Google APIs"
HOMEPAGE = "https://github.com/googleapis/python-api-common-protos"
AUTHOR = "Google LLC"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit pypi setuptools3

SRC_URI[sha256sum] = "6f1369b58ed6cf3a4b7054a44ebe8d03b29c309257583a2bbdc064cd1e4a1442"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-grpcio \
    ${PYTHON_PN}-protobuf \
"
