SUMMARY = "Test equality of unordered collections in pytest"
HOMEPAGE = "https://github.com/utapyngo/pytest-unordered"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fcd3af2d38a4d4dfd5138c6f163dbe2e"

SRC_URI[sha256sum] = "3c369ed86919d3eb35e11fd27bb679c1e3506ead9327e25aa8a07307256be65a"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-pytest"

BBCLASSEXTEND = "native nativesdk"

PYPI_PACKAGE = "pytest_unordered"
