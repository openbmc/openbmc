DESCRIPTION = "pytest-metadata is a plugin that allowed for accessing pytest metadata"
DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d425c8f3157dbf212db2ec53d9e5132"

SRC_URI[sha256sum] = "b7e6e0a45adacb17a03a97bf7a2ef60cc1f4e172bcce9732ce5e814191932315"
SRC_URI[md5sum] = "4fcf9764e6210c4555411fce8109e7cd"

PYPI_PACKAGE = "pytest-metadata"

inherit pypi setuptools3

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
