DESCRIPTION = "Google gRPC tools"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

DEPENDS_append = "${PYTHON_PN}-grpcio"
RDEPENDS_${PN} = "${PYTHON_PN}-grpcio"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

inherit pypi

SRC_URI[md5sum] = "b2fabfb54c7824c1e49a02de2aa6628e"
SRC_URI[sha256sum] = "4ce5aa660d7884f23aac1eafa93b97a4c3e2b512edff871e91fdb6ee86ebd5ea"

BBCLASSEXTEND = "native nativesdk"
