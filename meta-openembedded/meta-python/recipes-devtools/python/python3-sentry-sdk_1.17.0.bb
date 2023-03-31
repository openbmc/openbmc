SUMMARY = "The new Python SDK for Sentry.io"
DESCRIPTION = "This is the next line of the Python SDK \
for Sentry, intended to replace the raven package on PyPI."
HOMEPAGE = "https://github.com/getsentry/sentry-python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7fcb29c83dd48cb7b112d0dd81111a89"

RDEPENDS:${PN} += "\
	${PYTHON_PN}-urllib3 \
	${PYTHON_PN}-core \
	${PYTHON_PN}-json \
	${PYTHON_PN}-logging \
	${PYTHON_PN}-threading \
	${PYTHON_PN}-compression \
	${PYTHON_PN}-datetime \
"

SRC_URI[sha256sum] = "ad40860325c94d1a656da70fba5a7c4dbb2f6809d3cc2d00f74ca0b608330f14"

PYPI_PACKAGE = "sentry-sdk"

inherit pypi setuptools3
