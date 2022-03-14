SUMMARY = "unittest subTest() support and subtests fixture."
DESCRIPTION = "Adds support for TestCase.subTest.\
New subtests fixture, providing similar functionality for pure pytest tests."
HOMEPAGE = "https://github.com/pytest-dev/pytest-subtests"
BUGTRACKER = "https://github.com/pytest-dev/pytest-subtests/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=242b4e17fa287dcf7aef372f6bc3dcb1"

SRC_URI[sha256sum] = "3ebd306a8dcf75133f1742f288c82f36426ebcf8a132d4ee89782d20e84fc13a"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
