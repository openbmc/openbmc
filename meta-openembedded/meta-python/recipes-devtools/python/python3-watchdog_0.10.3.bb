SUMMARY = "Filesystem events monitoring"
DEPENDS = "${PYTHON_PN}-argh"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "9a5c9134e943b534b9f8b51d78338cf8"
SRC_URI[sha256sum] = "4214e1379d128b0588021880ccaf40317ee156d4603ac388b9adcf29165e0c04"

inherit pypi setuptools3

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-argh \
    ${PYTHON_PN}-pathtools3 \
    ${PYTHON_PN}-pyyaml \
    ${PYTHON_PN}-requests \
"

BBCLASSEXTEND = "native nativesdk"
