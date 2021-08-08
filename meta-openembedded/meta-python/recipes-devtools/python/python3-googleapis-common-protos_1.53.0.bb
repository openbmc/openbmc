DESCRIPTION = "Common protobufs used in Google APIs"
HOMEPAGE = "https://github.com/googleapis/python-api-common-protos"
AUTHOR = "Google LLC"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit pypi setuptools3

SRC_URI[sha256sum] = "a88ee8903aa0a81f6c3cec2d5cf62d3c8aa67c06439b0496b49048fb1854ebf4"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-grpcio \
    ${PYTHON_PN}-protobuf \
"
