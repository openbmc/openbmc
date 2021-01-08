DESCRIPTION = "pytest-metadata is a plugin that allowed for accessing pytest metadata"
DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d425c8f3157dbf212db2ec53d9e5132"

SRC_URI[sha256sum] = "71b506d49d34e539cc3cfdb7ce2c5f072bea5c953320002c95968e0238f8ecf1"

PYPI_PACKAGE = "pytest-metadata"

inherit pypi setuptools3

RDEPENDS_${PN} = " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
