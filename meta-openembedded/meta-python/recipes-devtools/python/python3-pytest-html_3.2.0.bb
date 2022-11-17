DESCRIPTION = "pytest plugin for generating html reports from test results"
DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d425c8f3157dbf212db2ec53d9e5132"

SRC_URI[sha256sum] = "c4e2f4bb0bffc437f51ad2174a8a3e71df81bbc2f6894604e604af18fbe687c3"

PYPI_PACKAGE = "pytest-html"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-git-archive-native"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
