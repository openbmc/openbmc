DESCRIPTION = "pytest-metadata is a plugin that allowed for accessing pytest metadata"
DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d425c8f3157dbf212db2ec53d9e5132"

SRC_URI[sha256sum] = "168d203abba8cabb65cf1b5fa675b0ba60dccbf1825d147960876a7e6f7c219c"

PYPI_PACKAGE = "pytest-metadata"

inherit pypi setuptools3

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
