DESCRIPTION = "pytest plugin for generating html reports from test results"
DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d425c8f3157dbf212db2ec53d9e5132"

SRC_URI[sha256sum] = "3ee1cf319c913d19fe53aeb0bc400e7b0bc2dbeb477553733db1dad12eb75ee3"


PYPI_PACKAGE = "pytest-html"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
