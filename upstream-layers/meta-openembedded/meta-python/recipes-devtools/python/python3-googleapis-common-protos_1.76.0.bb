DESCRIPTION = "Common protobufs used in Google APIs"
HOMEPAGE = "https://github.com/googleapis/python-api-common-protos"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "71db0e44a1d4c2dd024ebad44ca67129cc1e2892e4861df466764749d5ef8642"

RDEPENDS:${PN} += "\
    python3-grpcio \
    python3-protobuf \
"
