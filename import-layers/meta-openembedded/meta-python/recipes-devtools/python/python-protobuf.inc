DESCRIPTION = "Protocol Buffers"
HOMEPAGE = "https://developers.google.com/protocol-buffers/"
SECTION = "devel/python"

RDEPENDS_${PN} = "python-six"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

inherit pypi

SRC_URI[md5sum] = "bfc0c61c156a995e909521697e755780"
SRC_URI[sha256sum] = "ef02609ef445987976a3a26bff77119c518e0915c96661c3a3b17856d0ef6374"

# For usage in other recipies when compiling protobuf files (e.g. by grpcio-tools)
BBCLASSEXTEND = "native"
