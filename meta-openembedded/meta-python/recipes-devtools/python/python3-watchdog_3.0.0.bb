SUMMARY = "Filesystem events monitoring"
DEPENDS = "${PYTHON_PN}-argh"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "4d98a320595da7a7c5a18fc48cb633c2e73cda78f93cac2ef42d42bf609a33f9"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    ${PYTHON_PN}-argh \
    ${PYTHON_PN}-pathtools3 \
    ${PYTHON_PN}-pyyaml \
    ${PYTHON_PN}-requests \
"

BBCLASSEXTEND = "native nativesdk"
