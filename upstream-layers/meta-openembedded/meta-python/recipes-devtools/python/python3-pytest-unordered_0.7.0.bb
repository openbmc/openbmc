SUMMARY = "Test equality of unordered collections in pytest"
HOMEPAGE = "https://github.com/utapyngo/pytest-unordered"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fcd3af2d38a4d4dfd5138c6f163dbe2e"

SRC_URI[sha256sum] = "0f953a438db00a9f6f99a0f4727f2d75e72dd93319b3d548a97ec9db4903a44f"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-pytest"

BBCLASSEXTEND = "native nativesdk"

PYPI_PACKAGE = "pytest_unordered"
