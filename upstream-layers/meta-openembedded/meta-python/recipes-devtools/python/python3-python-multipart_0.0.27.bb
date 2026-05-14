SUMMARY = "A streaming multipart parser for Python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "9870a6a8c5a20a5bf4f07c017bd1489006ff8836cff097b6933355ee2b49b602"

inherit pypi python_hatchling ptest-python-pytest

PYPI_PACKAGE = "python_multipart"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"
CVE_PRODUCT = "python-multipart"

RDEPENDS:${PN}-ptest += " \
	python3-pyyaml \
"

