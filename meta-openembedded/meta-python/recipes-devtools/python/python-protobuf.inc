DESCRIPTION = "Protocol Buffers"
HOMEPAGE = "https://developers.google.com/protocol-buffers/"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

inherit pypi

SRC_URI[md5sum] = "ca881f18795e024a2b935d5bae9b1ebe"
SRC_URI[sha256sum] = "d831b047bd69becaf64019a47179eb22118a50dd008340655266a906c69c6417"

# http://errors.yoctoproject.org/Errors/Details/184715/
# Can't find required file: ../src/google/protobuf/descriptor.proto
CLEANBROKEN = "1"

UPSTREAM_CHECK_REGEX = "protobuf/(?P<pver>\d+(\.\d+)+)/"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-pkgutil \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-unittest \
"

# For usage in other recipies when compiling protobuf files (e.g. by grpcio-tools)
BBCLASSEXTEND = "native nativesdk"

