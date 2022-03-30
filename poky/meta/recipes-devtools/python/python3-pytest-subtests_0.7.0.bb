SUMMARY = "unittest subTest() support and subtests fixture."
DESCRIPTION = "Adds support for TestCase.subTest.\
New subtests fixture, providing similar functionality for pure pytest tests."
HOMEPAGE = "https://github.com/pytest-dev/pytest-subtests"
BUGTRACKER = "https://github.com/pytest-dev/pytest-subtests/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=242b4e17fa287dcf7aef372f6bc3dcb1"

SRC_URI[sha256sum] = "95c44c77e3fbede9848bb88ca90b384815fcba8090ef9a9f55659ab163b1681c"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
