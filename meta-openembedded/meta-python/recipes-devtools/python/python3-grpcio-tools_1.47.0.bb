DESCRIPTION = "Google gRPC tools"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-grpcio"

SRC_URI += "file://0001-setup.py-Do-not-mix-C-and-C-compiler-options.patch"
SRC_URI[sha256sum] = "f64b5378484be1d6ce59311f86174be29c8ff98d8d90f589e1c56d5acae67d3c"

RDEPENDS:${PN} = "${PYTHON_PN}-grpcio"

BBCLASSEXTEND = "native nativesdk"
