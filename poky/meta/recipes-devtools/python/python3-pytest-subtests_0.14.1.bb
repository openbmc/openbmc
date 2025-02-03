SUMMARY = "unittest subTest() support and subtests fixture."
DESCRIPTION = "Adds support for TestCase.subTest.\
New subtests fixture, providing similar functionality for pure pytest tests."
HOMEPAGE = "https://github.com/pytest-dev/pytest-subtests"
BUGTRACKER = "https://github.com/pytest-dev/pytest-subtests/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=242b4e17fa287dcf7aef372f6bc3dcb1"

SRC_URI[sha256sum] = "350c00adc36c3aff676a66135c81aed9e2182e15f6c3ec8721366918bbbf7580"

PYPI_PACKAGE = "pytest_subtests"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-attrs \
    python3-pytest \
"

BBCLASSEXTEND = "native nativesdk"
