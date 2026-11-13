DESCRIPTION = "Google gRPC channelz"
HOMEPAGE = "https://www.grpc.io/"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fe8c552b7748e45c00ac55ac0257f6c9"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "bab93349a820e714c6b827e374bb10896dbf6acfa45df6b6ccfa9af13301e4da"

DEPENDS += "python3-grpcio python3-wheel-native"

RDEPENDS:${PN} = "python3-grpcio"

BBCLASSEXTEND = "native nativesdk"
