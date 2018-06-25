DESCRIPTION = "Google gRPC tools"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

DEPENDS = "python-grpcio"
RDEPENDS_${PN} = "python-grpcio"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

inherit pypi setuptools

SRC_URI[md5sum] = "e04e65afe60410cf178ff2698f052a7c"
SRC_URI[sha256sum] = "edc84c09039d3a01012ccd97450abd06ee6b980710f6d9f191b50deb6774a75c"

# For usage in other recipies when compiling protobuf files (e.g. by grpcio-tools)
BBCLASSEXTEND = "native"
