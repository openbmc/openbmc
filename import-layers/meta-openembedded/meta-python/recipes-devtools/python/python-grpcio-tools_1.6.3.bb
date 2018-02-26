DESCRIPTION = "Google gRPC tools"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

DEPENDS = "python-grpcio"
RDEPENDS_${PN} = "python-grpcio"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

inherit pypi setuptools

SRC_URI[md5sum] = "835f19142e26cb7ad68cec3a083ecda2"
SRC_URI[sha256sum] = "57b3110ced50a18f1b2bf7da58ed011f57278b48084ed8013c67a34d42a5ca37"

# For usage in other recipies when compiling protobuf files (e.g. by grpcio-tools)
BBCLASSEXTEND = "native"
