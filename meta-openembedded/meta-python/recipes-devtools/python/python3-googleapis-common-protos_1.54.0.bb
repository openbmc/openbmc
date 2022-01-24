DESCRIPTION = "Common protobufs used in Google APIs"
HOMEPAGE = "https://github.com/googleapis/python-api-common-protos"
AUTHOR = "Google LLC"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit pypi setuptools3

SRC_URI[sha256sum] = "a4031d6ec6c2b1b6dc3e0be7e10a1bd72fb0b18b07ef9be7b51f2c1004ce2437"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-grpcio \
    ${PYTHON_PN}-protobuf \
"
