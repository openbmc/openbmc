SUMMARY = "Pytest plugin for measuring coverage."
HOMEPAGE = "https://github.com/pytest-dev/pytest-cov"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=cbc4e25353c748c817db2daffe605e43 \
"

SRC_URI[sha256sum] = "46935f7aaefba760e716c2ebfbe1c216240b9592966e7da99ea8292d4d3e2a0a"

inherit pypi setuptools3

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "python3-coverage python3-pytest python3-pluggy"

PYPI_PACKAGE = "pytest_cov"

BBCLASSEXTEND = "native nativesdk"
