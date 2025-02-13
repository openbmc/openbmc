DESCRIPTION = "Google gRPC reflection"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

inherit pypi setuptools3

DEPENDS += "python3-grpcio"
PYPI_PACKAGE = "grpcio_reflection"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "af46ce13e57fd7602deaef5b1d1903f6644f8982cc00f62fa9fa5b928acf6a49"

RDEPENDS:${PN} = "python3-grpcio"

BBCLASSEXTEND = "native nativesdk"
