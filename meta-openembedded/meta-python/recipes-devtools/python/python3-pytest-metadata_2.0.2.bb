DESCRIPTION = "pytest-metadata is a plugin that allowed for accessing pytest metadata"
DEPENDS += "python3-setuptools-scm-native"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d425c8f3157dbf212db2ec53d9e5132"

SRC_URI[sha256sum] = "fcd2f416f15be295943527b3c8ba16a44ae5a7141939c90c3dc5ce9d167cf2a5"

PYPI_PACKAGE = "pytest-metadata"

inherit pypi setuptools3

RDEPENDS:${PN} = " \
    python3-pytest \
"

BBCLASSEXTEND = "native nativesdk"
