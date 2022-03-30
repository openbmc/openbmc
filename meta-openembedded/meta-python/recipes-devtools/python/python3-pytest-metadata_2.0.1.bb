DESCRIPTION = "pytest-metadata is a plugin that allowed for accessing pytest metadata"
DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d425c8f3157dbf212db2ec53d9e5132"

SRC_URI[sha256sum] = "5cdb6aeea8ba9109181cf9f149c8a3ae1430ff7e44506a8f866af8a98ca46301"

PYPI_PACKAGE = "pytest-metadata"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
