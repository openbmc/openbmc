DESCRIPTION = "Google gRPC"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

DEPENDS_append = "${PYTHON_PN}-protobuf"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-grpcio:"

SRC_URI_append_class-target = " file://0001-setup.py-Do-not-mix-C-and-C-compiler-options.patch \
                                file://ppc-boringssl-support.patch \
                                file://riscv64_support.patch \
"

RDEPENDS_${PN} = "${PYTHON_PN}-protobuf \
                  ${PYTHON_PN}-setuptools \
                  ${PYTHON_PN}-six \
"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

inherit pypi

SRC_URI[md5sum] = "188565f150f34d1927b87e014f0f4b73"
SRC_URI[sha256sum] = "2ddbca16c2e7b3f2ffc6e34c7cfa6886fb01de9f156ad3f77b72ad652d632097"

CLEANBROKEN = "1"

BBCLASSEXTEND = "native nativesdk"
