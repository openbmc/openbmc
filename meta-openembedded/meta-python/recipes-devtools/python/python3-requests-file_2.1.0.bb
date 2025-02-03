SUMMARY = "File transport adapter for Requests"
HOMEPAGE = "http://github.com/dashea/requests-file"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9cc728d6087e43796227b0a31422de6b"

SRC_URI[sha256sum] = "0f549a3f3b0699415ac04d167e9cb39bccfb730cb832b4d20be3d9867356e658"

PYPI_PACKAGE = "requests_file"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-requests \
"

