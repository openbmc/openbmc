SUMMARY = "A streaming multipart parser for Python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "08fadc45918cd615e26846437f50c5d6d23304da32c341f289a617127b081f17"

inherit pypi python_hatchling ptest-python-pytest

PYPI_PACKAGE = "python_multipart"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"
CVE_PRODUCT = "python-multipart"

RDEPENDS:${PN}-ptest += " \
	python3-pyyaml \
"

