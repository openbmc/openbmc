DESCRIPTION = "Google API client core library"
HOMEPAGE = "https://github.com/googleapis/python-api-core"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "5ab9a396965c1227d7dea4581e562b7018aa2bca96d2de0be5eb70dc9c8ab3a1"

RDEPENDS:${PN} += "\
    python3-asyncio \
    python3-datetime \
    python3-logging \
    python3-math \
"

RDEPENDS:${PN} += "\
    python3-googleapis-common-protos \
    python3-google-auth \
    python3-grpcio \
    python3-opentelemetry-api \
    python3-proto-plus \
    python3-protobuf \
    python3-requests \
"
