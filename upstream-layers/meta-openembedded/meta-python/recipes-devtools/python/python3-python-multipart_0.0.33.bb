SUMMARY = "A streaming multipart parser for Python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[sha256sum] = "b8112424a6abd162ec08d390565dd113ad699f2fe6fef3c987b8bbcbfe9e390d"

inherit pypi python_hatchling ptest-python-pytest

CVE_PRODUCT = "python-multipart"

RDEPENDS:${PN}-ptest += " \
	python3-pyyaml \
"
