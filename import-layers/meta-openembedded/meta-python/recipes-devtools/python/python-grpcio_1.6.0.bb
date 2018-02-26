DESCRIPTION = "Google gRPC"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

DEPENDS = "python-protobuf"

SRC_URI += "file://0001-setup.py-Do-not-mix-C-and-C-compiler-options.patch \
           "
RDEPENDS_${PN} = "python-enum34 \
                  python-futures \
                  python-protobuf \
                  python-setuptools \
                  python-six \
"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

inherit pypi setuptools

SRC_URI[md5sum] = "c91b8c105ff4163b41790f3b63db92e1"
SRC_URI[sha256sum] = "6cd793d515ec53587a8968f2a53647f1449763ad06be0d287e3c1e47418e1e50"

# For usage in other recipes when compiling protobuf files (e.g. by grpcio-tools)
BBCLASSEXTEND = "native"
