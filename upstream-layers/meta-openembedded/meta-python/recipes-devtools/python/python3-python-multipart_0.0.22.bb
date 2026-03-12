SUMMARY = "A streaming multipart parser for Python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3d98f0d58b28321924a89ab60c82410e"

SRC_URI[sha256sum] = "7340bef99a7e0032613f56dc36027b959fd3b30a787ed62d310e951f7c3a3a58"

inherit pypi python_hatchling ptest-python-pytest

PYPI_PACKAGE = "python_multipart"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"
CVE_PRODUCT = "python-multipart"

RDEPENDS:${PN}-ptest += " \
	python3-pyyaml \
"

