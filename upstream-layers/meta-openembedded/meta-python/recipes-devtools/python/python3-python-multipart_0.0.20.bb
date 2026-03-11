SUMMARY = "A streaming multipart parser for Python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3d98f0d58b28321924a89ab60c82410e"

SRC_URI[sha256sum] = "8dd0cab45b8e23064ae09147625994d090fa46f5b0d1e13af944c331a7fa9d13"

inherit pypi python_hatchling ptest-python-pytest

PYPI_PACKAGE = "python_multipart"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN}-ptest += " \
	python3-pyyaml \
"

