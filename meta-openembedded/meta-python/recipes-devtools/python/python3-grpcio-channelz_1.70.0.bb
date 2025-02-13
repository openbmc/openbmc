DESCRIPTION = "Google gRPC channelz"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

inherit pypi setuptools3

DEPENDS += "python3-grpcio"
PYPI_PACKAGE = "grpcio_channelz"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "79334e27f6d9bfbfc536adfa2a4108a1ac048a4d052158f1cc1df6cc359e748a"

RDEPENDS:${PN} = "python3-grpcio"

BBCLASSEXTEND = "native nativesdk"
