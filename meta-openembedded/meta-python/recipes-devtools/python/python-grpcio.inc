DESCRIPTION = "Google gRPC"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

DEPENDS_append = "${PYTHON_PN}-protobuf"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-grpcio:"
SRC_URI_append_class-target = " file://0001-setup.py-Do-not-mix-C-and-C-compiler-options.patch "

RDEPENDS_${PN} = "${PYTHON_PN}-protobuf \
                  ${PYTHON_PN}-setuptools \
                  ${PYTHON_PN}-six \
"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

inherit pypi

SRC_URI[md5sum] = "0987ae2e47b5103de3cacc437874a3c5"
SRC_URI[sha256sum] = "4bf23666e763ca7ff6010465864e9f088f4ac7ecc1e11abd6f85b250e66b2c05"

CLEANBROKEN = "1"

BBCLASSEXTEND = "native nativesdk"
