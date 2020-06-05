DESCRIPTION = "pytest plugin for generating html reports from test results"
DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://../pytest-html.LICENSE;md5=5d425c8f3157dbf212db2ec53d9e5132"

# Per README.rst the license statement is fetched from
# https://raw.githubusercontent.com/davehunt/pytest-html/master/LICENSE
SRC_URI += "https://raw.githubusercontent.com/davehunt/pytest-html/master/LICENSE;name=license;downloadfilename=pytest-html.LICENSE"
SRC_URI[license.md5sum] = "5d425c8f3157dbf212db2ec53d9e5132"
SRC_URI[license.sha256sum] = "2bfdca60adf803108d4c7f009000bea76ad00e621e163197881b0eaae91b530e"

SRC_URI[sha256sum] = "6a4ac391e105e391208e3eb9bd294a60dd336447fd8e1acddff3a6de7f4e57c5"


PYPI_PACKAGE = "pytest-html"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
