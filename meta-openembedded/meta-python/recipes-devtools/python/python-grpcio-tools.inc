DESCRIPTION = "Google gRPC tools"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

DEPENDS_append = "${PYTHON_PN}-grpcio"
RDEPENDS_${PN} = "${PYTHON_PN}-grpcio"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

inherit pypi

SRC_URI[md5sum] = "b2fabfb54c7824c1e49a02de2aa6628e"
SRC_URI[sha256sum] = "4ce5aa660d7884f23aac1eafa93b97a4c3e2b512edff871e91fdb6ee86ebd5ea"

BBCLASSEXTEND = "native nativesdk"
