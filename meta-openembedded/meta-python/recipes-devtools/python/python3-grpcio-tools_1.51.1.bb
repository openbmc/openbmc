DESCRIPTION = "Google gRPC tools"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-grpcio"

SRC_URI += "file://0001-setup.py-Do-not-mix-C-and-C-compiler-options.patch \
            file://0001-protobuf-Disable-musttail-attribute-on-mips.patch \
            "
SRC_URI[sha256sum] = "8e62d23d3fed9d4f81738f98dd193dbd2e21aed4a8f0dd715e75b5439e649727"

RDEPENDS:${PN} = "${PYTHON_PN}-grpcio"

BBCLASSEXTEND = "native nativesdk"
