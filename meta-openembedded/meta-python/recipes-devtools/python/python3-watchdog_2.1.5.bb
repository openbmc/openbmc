SUMMARY = "Filesystem events monitoring"
DEPENDS = "${PYTHON_PN}-argh"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "5563b005907613430ef3d4aaac9c78600dd5704e84764cb6deda4b3d72807f09"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    ${PYTHON_PN}-argh \
    ${PYTHON_PN}-pathtools3 \
    ${PYTHON_PN}-pyyaml \
    ${PYTHON_PN}-requests \
"

BBCLASSEXTEND = "native nativesdk"
