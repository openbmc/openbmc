DESCRIPTION = "Google gRPC channelz"
HOMEPAGE = "https://www.grpc.io/"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

inherit pypi setuptools3

DEPENDS += "python3-grpcio"
PYPI_PACKAGE = "grpcio_channelz"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "2ab15ef7b2252a9ba31df500bbe295ce5310a8b01ea6f6d67a17172d914a41a8"

RDEPENDS:${PN} = "python3-grpcio"

BBCLASSEXTEND = "native nativesdk"
