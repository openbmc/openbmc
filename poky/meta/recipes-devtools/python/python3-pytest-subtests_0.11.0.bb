SUMMARY = "unittest subTest() support and subtests fixture."
DESCRIPTION = "Adds support for TestCase.subTest.\
New subtests fixture, providing similar functionality for pure pytest tests."
HOMEPAGE = "https://github.com/pytest-dev/pytest-subtests"
BUGTRACKER = "https://github.com/pytest-dev/pytest-subtests/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=242b4e17fa287dcf7aef372f6bc3dcb1"

SRC_URI[sha256sum] = "51865c88457545f51fb72011942f0a3c6901ee9e24cbfb6d1b9dc1348bafbe37"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-pytest \
"

BBCLASSEXTEND = "native nativesdk"
