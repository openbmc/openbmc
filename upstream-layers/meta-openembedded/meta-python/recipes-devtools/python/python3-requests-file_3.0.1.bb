SUMMARY = "File transport adapter for Requests"
HOMEPAGE = "https://github.com/dashea/requests-file"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9cc728d6087e43796227b0a31422de6b"

SRC_URI[sha256sum] = "f14243d7796c588f3521bd423c5dea2ee4cc730e54a3cac9574d78aca1272576"

PYPI_PACKAGE = "requests_file"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta ptest-python-pytest

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
    python3-requests \
"

