SUMMARY = "unittest subTest() support and subtests fixture."
DESCRIPTION = "Adds support for TestCase.subTest.\
New subtests fixture, providing similar functionality for pure pytest tests."
HOMEPAGE = "https://github.com/pytest-dev/pytest-subtests"
BUGTRACKER = "https://github.com/pytest-dev/pytest-subtests/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=242b4e17fa287dcf7aef372f6bc3dcb1"

SRC_URI[sha256sum] = "46eb376022e926950816ccc23502de3277adcc1396652ddb3328ce0289052c4d"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
